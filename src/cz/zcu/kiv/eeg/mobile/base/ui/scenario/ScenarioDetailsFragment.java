package cz.zcu.kiv.eeg.mobile.base.ui.scenario;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import cz.zcu.kiv.eeg.mobile.base.R;
import cz.zcu.kiv.eeg.mobile.base.data.container.xml.Scenario;
import cz.zcu.kiv.eeg.mobile.base.utils.FileUtils;

/**
 * Fragment for displaying scenario details.
 *
 * @author Petr Miko
 */
public class ScenarioDetailsFragment extends Fragment {

    public final static String TAG = ScenarioDetailsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean hasData = getArguments().getParcelable("data") != null && getArguments().getInt("index", -1) >= 0;

        if (hasData) {
            View view = inflater.inflate(R.layout.base_scenario_details, container, false);
            initData(view);
            return view;
        } else {
            return inflater.inflate(R.layout.details_empty, container, false);
        }
    }

    /**
     * Initializes view elements and sets proper data into them.
     *
     * @param view view to be displayed
     */
    private void initData(View view) {

        // obtaining view elements
        TextView scenarioId = (TextView) view.findViewById(R.id.scenarioId);
        TextView scenarioName = (TextView) view.findViewById(R.id.scenarioName);
        TextView scenarioType = (TextView) view.findViewById(R.id.scenarioMime);
        TextView fileName = (TextView) view.findViewById(R.id.scenarioFileName);
        TextView fileSize = (TextView) view.findViewById(R.id.scenarioFileLength);
        TextView description = (TextView) view.findViewById(R.id.scenarioDescription);

        Scenario scenario = (Scenario) getArguments().getParcelable("data");
        if (scenario != null) {
            scenarioId.setText(Integer.toString(scenario.getScenarioId()));
            scenarioName.setText(scenario.getScenarioName());
            scenarioType.setText(scenario.getMimeType());
            fileName.setText(scenario.getFileName());
            fileSize.setText(FileUtils.getFileSize(Long.parseLong(scenario.getFileLength())));
            description.setText(scenario.getDescription());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}