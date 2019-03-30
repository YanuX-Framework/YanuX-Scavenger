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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.service.PersistentService;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class BeaconAdvertiser {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + PersistentService.class.getSimpleName();
    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private final BeaconParser beaconParser;
    private final BeaconTransmitter beaconTransmitter;

    private Context context;
    private Preferences preferences;

    public BeaconAdvertiser(Context context) {
        this.context = context;
        this.preferences = new Preferences(context);
        this.beaconParser = new BeaconParser().setBeaconLayout(IBEACON_LAYOUT);
        this.beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        this.beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        this.beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
    }

    public void start() {
        int result = BeaconTransmitter.checkTransmissionSupported(context);
        if(result == BeaconTransmitter.SUPPORTED) {
            Log.d(LOG_TAG, "Bluetooth Low Energy Advertisment is SUPPORTED.");
            Beacon beacon = new Beacon.Builder()
                    .setId1(preferences.getBeaconAdvertiserParametersUuid())
                    .setId2(Integer.toString(preferences.getBeaconAdvertiserParametersMajor()))
                    .setId3(Integer.toString(preferences.getBeaconAdvertiserParametersMinor()))
                    .setManufacturer(0x004c)
                    .setTxPower(-59)
                    .build();

            beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    super.onStartSuccess(settingsInEffect);
                    Log.d(LOG_TAG, "Bluetooth Low Energy Advertisment STARTED.");
                }
                @Override
                public void onStartFailure(int errorCode) {
                    super.onStartFailure(errorCode);
                    Log.d(LOG_TAG, "Bluetooth Low Energy Advertisment could NOT be started.");
                }
            });
        } else {
            Log.d(LOG_TAG, "Bluetooth Low Energy Advertisment is NOT supported.");
            Toast.makeText(context, R.string.beacon_advertiser_not_supported, Toast.LENGTH_LONG).show();
        }
    }

    public void stop() {
        beaconTransmitter.stopAdvertising();
    }
}
