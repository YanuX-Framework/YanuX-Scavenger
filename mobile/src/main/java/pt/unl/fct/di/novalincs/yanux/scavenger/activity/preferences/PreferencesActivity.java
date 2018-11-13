package pt.unl.fct.di.novalincs.yanux.scavenger.activity.preferences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


import androidx.preference.PreferenceFragmentCompat;
import pt.unl.fct.di.novalincs.yanux.scavenger.R;

public class PreferencesActivity extends AppCompatActivity {
    public static class PreferencesFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_preferences);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_container, new PreferencesFragment())
                .commit();
    }
}
