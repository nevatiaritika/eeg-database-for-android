package cz.zcu.kiv.eeg.mobile.base.ws.asynctask;

import android.content.SharedPreferences;
import android.util.Log;
import cz.zcu.kiv.eeg.mobile.base.R;
import cz.zcu.kiv.eeg.mobile.base.archetypes.CommonActivity;
import cz.zcu.kiv.eeg.mobile.base.archetypes.CommonService;
import cz.zcu.kiv.eeg.mobile.base.data.Values;
import cz.zcu.kiv.eeg.mobile.base.data.adapter.ScenarioAdapter;
import cz.zcu.kiv.eeg.mobile.base.data.container.xml.Scenario;
import cz.zcu.kiv.eeg.mobile.base.data.container.xml.ScenarioList;
import cz.zcu.kiv.eeg.mobile.base.ws.ssl.HttpsClient;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static cz.zcu.kiv.eeg.mobile.base.data.ServiceState.*;

/**
 * Common service (Asynctask) for fetching scenarios from eeg base.
 *
 * @author Petr Miko
 */
public class FetchScenarios extends CommonService<Void, Void, List<Scenario>> {

    private static final String TAG = FetchScenarios.class.getSimpleName();
    private final ScenarioAdapter scenarioAdapter;
    private final String qualifier;

    /**
     * Constructor.
     *
     * @param activity        parent activity
     * @param scenarioAdapter adapter for holding collection of scenarios
     * @param qualifier       qualifier to distinguish whether to fetch private or public data
     */
    public FetchScenarios(CommonActivity activity, ScenarioAdapter scenarioAdapter, String qualifier) {
        super(activity);
        this.scenarioAdapter = scenarioAdapter;
        this.qualifier = qualifier;
    }

    /**
     * Method, where all scenarios are read from server.
     * All heavy lifting is made here.
     *
     * @param params omitted here
     * @return list of fetched scenarios
     */
    @Override
    protected List<Scenario> doInBackground(Void... params) {
        SharedPreferences credentials = getCredentials();
        String username = credentials.getString("username", null);
        String password = credentials.getString("password", null);
        String url = credentials.getString("url", null) + Values.SERVICE_SCENARIOS + qualifier;

        setState(RUNNING, R.string.working_ws_scenarios);
        HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        HttpEntity<Object> entity = new HttpEntity<Object>(requestHeaders);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpsClient.getClient()));
        restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

        try {
            // Make the network request
            Log.d(TAG, url);
            ResponseEntity<ScenarioList> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    ScenarioList.class);
            ScenarioList body = response.getBody();

            if (body != null) {
                return body.getScenarios();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            setState(ERROR, e);
        } finally {
            setState(DONE);
        }
        return Collections.emptyList();
    }

    /**
     * Fetched scenarios are put into scenario adapter and sorted.
     *
     * @param resultList fetched scenarios
     */
    @Override
    protected void onPostExecute(List<Scenario> resultList) {
        scenarioAdapter.clear();
        if (resultList != null && !resultList.isEmpty()) {
            Collections.sort(resultList, new Comparator<Scenario>() {
                @Override
                public int compare(Scenario lhs, Scenario rhs) {
                    return lhs.getScenarioId() - rhs.getScenarioId();
                }
            });

            for (Scenario scenario : resultList) {
                scenarioAdapter.add(scenario);
            }
        }
    }

}
