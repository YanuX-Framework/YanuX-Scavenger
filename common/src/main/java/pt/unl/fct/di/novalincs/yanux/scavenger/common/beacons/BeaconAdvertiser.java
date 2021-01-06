/*
 * Copyright (c) 2021 Pedro Albuquerque Santos.
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

import android.bluetooth.BluetoothAdapter;
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
    private BeaconTransmitter beaconTransmitter;

    private final Context context;
    private final Preferences preferences;

    public BeaconAdvertiser(Context context) {
        this.context = context;
        this.preferences = new Preferences(context);
        this.beaconParser = new BeaconParser().setBeaconLayout(IBEACON_LAYOUT);
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            this.beaconTransmitter = new BeaconTransmitter(context, beaconParser);
            //TODO: Allow setting TX Power Lovel and Adversite Mode through preferences
            switch (this.preferences.getBeaconAdvertiserPowerLevel()) {
                case "HIGH":
                    this.beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
                    break;
            }

            switch (this.preferences.getBeaconAdvertiserPowerLevel()) {
                case "MEDIUM":
                    this.beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
                    break;
                case "LOW":
                    this.beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW);
                    break;
                case "ULTRA_LOW":
                    this.beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW);
                    break;
                default:
                    this.beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
                    break;
            }

            this.beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        } else {
            Log.d(LOG_TAG, "Bluetooth Adapter NOT FOUND!");
        }
    }

    public void start() {
        int result = BeaconTransmitter.checkTransmissionSupported(context);
        if (result == BeaconTransmitter.SUPPORTED) {
            Log.d(LOG_TAG, "Bluetooth Low Energy Advertisment is SUPPORTED.");
            Beacon beacon = new Beacon.Builder()
                    .setId1(preferences.getBeaconAdvertiserParametersUuid())
                    .setId2(Integer.toString(preferences.getBeaconAdvertiserParametersMajor()))
                    .setId3(Integer.toString(preferences.getBeaconAdvertiserParametersMinor()))
                    .setManufacturer(0x004c)
                    .setTxPower(-59)
                    .build();
            if (beaconTransmitter != null) {
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
            }
        } else {
            Log.d(LOG_TAG, "Bluetooth Low Energy Advertisment is NOT supported.");
            Toast.makeText(context, R.string.beacon_advertiser_not_supported, Toast.LENGTH_LONG).show();
        }
    }

    public void stop() {
        if (beaconTransmitter != null) {
            beaconTransmitter.stopAdvertising();
        }
    }
}
