package cz.zcu.kiv.eeg.mobile.base.ui.startup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import cz.zcu.kiv.eeg.mobile.base.R;
import cz.zcu.kiv.eeg.mobile.base.archetypes.CommonActivity;
import cz.zcu.kiv.eeg.mobile.base.data.Values;
import cz.zcu.kiv.eeg.mobile.base.db.asynctask.TestCredentials;
import cz.zcu.kiv.eeg.mobile.base.utils.ConnectionUtils;
import cz.zcu.kiv.eeg.mobile.base.utils.ValidationUtils;
import net.rehacktive.wasp.WaspDb;
import net.rehacktive.wasp.WaspFactory;
import net.rehacktive.wasp.WaspHash;

/**
 * Activity for user's log in process.
 *
 * @author Petr Miko
 */
public class WelcomeActivity extends CommonActivity {

    private final static String TAG = WelcomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Welcome activity displayed");
        setContentView(R.layout.base_welcome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuLogin:
                loginClick();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles login button click event. Grabs credentials and endpoint from fields and tests credentials.
     */
    public void loginClick()
    {

    }

    /**
     * Method for testing credentials.
     * If online, credentials validated and if no error found, TestCredentials service is invoked.
     *
     * @param username credentials username
     * @param password credentials password
     * @param url      eeg base rest endpoint
     */
    private void testCredentials(String username, String password, String url) {

        if (!ConnectionUtils.isOnline(this)) {
            showAlert(getString(R.string.error_offline));
            return;
        }

        SharedPreferences credentials = getSharedPreferences(Values.PREFS_CREDENTIALS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = credentials.edit();

        StringBuilder error = new StringBuilder();

        if (ValidationUtils.isUsernameFormatInvalid(username))
            error.append(getString(R.string.error_invalid_username)).append('\n');
        if (ValidationUtils.isPasswordFormatInvalid(password))
            error.append(getString(R.string.error_invalid_password)).append('\n');
        if (ValidationUtils.isUrlFormatInvalid(url))
            error.append(getString(R.string.error_invalid_url)).append('\n');

        if (error.toString().isEmpty()) {

            editor.putString("tmp_username", username);
            editor.putString("tmp_password", password);
            editor.putString("tmp_url", url + Values.ENDPOINT);

            editor.commit();

            new TestCredentials(this, true).execute();
        } else {
            showAlert(error.toString());
        }
    }
}
