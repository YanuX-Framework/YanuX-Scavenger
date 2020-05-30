/*
 * Copyright (c) 2020 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.service;

import android.content.SharedPreferences;
import android.util.Log;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class PersistentServiceSharedPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + PersistentServiceSharedPreferenceChangeListener.class.getSimpleName();

    private PersistentService service;

    public PersistentServiceSharedPreferenceChangeListener(PersistentService service) {
        this.service = service;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "OnSharedPreferenceChangeListener: " + key);
        if (key.equals(Preferences.PREFERENCE_YANUX_BROKER_URL)) {
            service.start();
        } else if (key.equals(Preferences.PREFERENCE_YANUX_AUTH_OAUTH2_AUTHORIZATION_SERVER_URL)) {
            service.userAuthorization();
        } else if (key.equals(Preferences.ALLOW_ZEROCONF)) {
            if (service.getPreferences().isZeroconfAllowed()) {
                service.getZeroconf().startDiscovery();
            } else {
                service.getZeroconf().stopDiscovery();
            }
        } else if (key.equals(Preferences.SHOULD_BEACON_SCAN)) {
            if (service.getPreferences().shouldBeaconScan()) {
                service.startBeaconScan();
            } else {
                service.stopBeaconScan();
            }
        } else if (key.equals(Preferences.SHOULD_BEACON_ADVERTISE)) {
            if (service.getPreferences().shouldBeaconAdvertise()) {
                service.startBeaconAdvertisement();
            } else {
                service.stopBeaconAdvertisement();
            }
        }
    }
}
