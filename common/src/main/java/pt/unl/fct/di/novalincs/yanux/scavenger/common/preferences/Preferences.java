/*
 * Copyright (c) 2016 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    public static final String INVALID = null;
    private static final String PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE = "asked_wifi_scanning_always_available";
    private static final boolean PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE_DEFAULT = false;
    private static final String PREFERENCE_LOG_NAME = "log_name";
    private static final String PREFERENCE_LOG_NAME_DEFAULT = "log";
    private static final String PREFERENCE_LOG_SAMPLES = "log_samples";
    private static final int PREFERENCE_LOG_SAMPLES_DEFAULT = 10;

    private final Context context;
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor preferencesEditor;

    public Preferences(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.preferencesEditor = preferences.edit();
    }

    public boolean hasAskedForWifiScanningAlwaysAvailable() {
        return preferences.getBoolean(PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE,
                PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE_DEFAULT);
    }

    public void setHasAskedForWifiScanningAlwaysAvailable(boolean flag) {
        preferencesEditor.putBoolean(PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE, flag);
        preferencesEditor.apply();
    }

    public String getLogName() {
        return preferences.getString(PREFERENCE_LOG_NAME, PREFERENCE_LOG_NAME_DEFAULT);
    }

    public void setLogName(String logName) {
        preferencesEditor.putString(PREFERENCE_LOG_NAME, logName);
        preferencesEditor.apply();
    }

    public int getLogSamples() {
        return preferences.getInt(PREFERENCE_LOG_SAMPLES, PREFERENCE_LOG_SAMPLES_DEFAULT);
    }

    public void setLogSamples(int samples) {
        preferencesEditor.putInt(PREFERENCE_LOG_SAMPLES, samples);
        preferencesEditor.apply();
    }

}