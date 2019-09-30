/*
 * Copyright (c) 2019 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.service.MobilePersistentService;

public class PreferencesActivity extends AppCompatActivity {
    public static void refresh(AppCompatActivity activity) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_container, new PreferencesFragment())
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_preferences);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh(this);
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        private Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = getContext();

            CustomInputTypeOnBindEditTextListener numericOnBitTextListener = new CustomInputTypeOnBindEditTextListener(InputType.TYPE_CLASS_NUMBER);
            CustomInputTypeOnBindEditTextListener uriOnBitTextListener = new CustomInputTypeOnBindEditTextListener(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);

            ((EditTextPreference) findPreference(Preferences.PREFERENCE_BEACON_MATCHER_PARAMETERS_MAJOR)).setOnBindEditTextListener(numericOnBitTextListener);
            ((EditTextPreference) findPreference(Preferences.PREFERENCE_BEACON_MATCHER_PARAMETERS_MINOR)).setOnBindEditTextListener(numericOnBitTextListener);
            ((EditTextPreference) findPreference(Preferences.PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MAJOR)).setOnBindEditTextListener(numericOnBitTextListener);
            ((EditTextPreference) findPreference(Preferences.PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MINOR)).setOnBindEditTextListener(numericOnBitTextListener);
            ((EditTextPreference) findPreference(Preferences.PREFERENCE_BEACONS_REFRESH_INTERVAL)).setOnBindEditTextListener(numericOnBitTextListener);
            ((EditTextPreference) findPreference(Preferences.PREFERENCE_BEACONS_INACTIVITY_TIMER)).setOnBindEditTextListener(numericOnBitTextListener);
            ((EditTextPreference) findPreference(Preferences.PREFERENCE_YANUX_AUTH_OAUTH2_AUTHORIZATION_SERVER_URL)).setOnBindEditTextListener(uriOnBitTextListener);
            ((EditTextPreference) findPreference(Preferences.PREFERENCE_YANUX_AUTH_REDIRECT_URI)).setOnBindEditTextListener(uriOnBitTextListener);
            ((EditTextPreference) findPreference(Preferences.PREFERENCE_YANUX_BROKER_URL)).setOnBindEditTextListener(uriOnBitTextListener);
            ((EditTextPreference) findPreference(Preferences.PREFERENCE_HTTP_SERVER_PORT)).setOnBindEditTextListener(numericOnBitTextListener);

            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Preferences.ALLOW_PERSISTENT_SERVICE)) {
                if (getActivity() instanceof AppCompatActivity) {
                    refresh((AppCompatActivity) getActivity());
                }
                if (sharedPreferences.getBoolean(Preferences.ALLOW_PERSISTENT_SERVICE, Preferences.ALLOW_PERSISTENT_SERVICE_DEFAULT)) {
                    MobilePersistentService.start(context);
                } else {
                    MobilePersistentService.stop(context);
                }
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }
}
