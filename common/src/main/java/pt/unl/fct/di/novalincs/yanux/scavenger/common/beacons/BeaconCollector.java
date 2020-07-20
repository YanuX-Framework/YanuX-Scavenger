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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BleNotAvailableException;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class BeaconCollector {
    public static final String ACTION_BEACON_MONITOR_ENTER_REGION = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BeaconCollector.ACTION_BEACON_MONITOR_ENTER_REGION";
    public static final String ACTION_BEACON_MONITOR_EXIT_REGION = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BeaconCollector.ACTION_BEACON_MONITOR_EXIT_REGION";
    public static final String ACTION_BEACON_MONITOR_DETERMINED_REGION_STATE = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BeaconCollector.ACTION_BEACON_MONITOR_DETERMINED_REGION_STATE";
    public static final String ACTION_BEACON_RANGE_BEACONS = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BeaconCollector.ACTION_BEACON_RANGE_BEACONS";
    public static final String EXTRA_BEACON_REGION = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BeaconCollector.EXTRA_BEACON_REGION";
    public static final String EXTRA_BEACON_REGION_STATE = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BeaconCollector.EXTRA_BEACON_REGION_STATE";
    public static final String EXTRA_BEACONS = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BeaconCollector.EXTRA_BEACONS";
    private static final String LOG_TAG = Constants.LOG_TAG + "_BEACON_COLLECTOR";
    //iBeacon Beacon Layout
    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String REGION_UUID = "3c8bc916-4b9c-4777-98e1-9f6b8a789054";

    private final Context context;
    private final BeaconConsumer beaconConsumer;
    private final BroadcastReceiver broadcastReceiver;
    private final IntentFilter intentFilter;
    private final BeaconManager beaconManager;
    private Region region;
    private Preferences preferences;
    private PermissionManager permissionManager;

    private long startRangingTime;
    private boolean ranging;
    private boolean monitoring;

    public BeaconCollector(BeaconConsumer beaconConsumer, BroadcastReceiver broadcastReceiver) {
        this.beaconConsumer = beaconConsumer;
        context = (Context) beaconConsumer;
        if (context instanceof AppCompatActivity) {
            permissionManager = new PermissionManager((AppCompatActivity) context);
            preferences = new Preferences(context);
        }
        this.broadcastReceiver = broadcastReceiver;
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BEACON_MONITOR_ENTER_REGION);
        intentFilter.addAction(ACTION_BEACON_MONITOR_EXIT_REGION);
        intentFilter.addAction(ACTION_BEACON_MONITOR_DETERMINED_REGION_STATE);
        intentFilter.addAction(ACTION_BEACON_RANGE_BEACONS);

        BeaconManager.setRssiFilterImplClass(CustomRunningAverageRssiFilter.class);
        if (preferences != null) {
            // TODO: Perhaps I should add a dedicated preference for this!
            CustomRunningAverageRssiFilter.setSampleExpirationMilliseconds(preferences.getBeaconsInactivityTimer());
        }

        beaconManager = BeaconManager.getInstanceForApplication(context);
        try {
            beaconManager.checkAvailability();
        } catch (BleNotAvailableException ex) {
            Toast.makeText(context, R.string.beacon_bluetooth_le_not_available, Toast.LENGTH_LONG).show();
        }

        beaconManager.getBeaconParsers().add(new BeaconParser("iBeacon").setBeaconLayout(IBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser("AltBeacon").setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser("Eddystone-UID").setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser("Eddystone-URL").setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser("Eddystone-TLM").setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser("UriBeacon").setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT));

        /*
         * TODO:
         * If needed and possible, implement a better distance calculator.
         */
        //Beacon.setDistanceCalculator(new CustomDistanceCalculator());
        beaconManager.setBackgroundMode(false);
        beaconManager.setForegroundScanPeriod(100);
        beaconManager.setForegroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(100);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.bind(this.beaconConsumer);

        //Monitor Notifier
        setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(LOG_TAG, "Region Entered");
                Intent intent = new Intent();
                intent.setAction(ACTION_BEACON_MONITOR_ENTER_REGION);
                intent.putExtra(EXTRA_BEACON_REGION, (Parcelable) region);
                context.sendBroadcast(intent);
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d(LOG_TAG, "Region Exit");
                Intent intent = new Intent();
                intent.setAction(ACTION_BEACON_MONITOR_EXIT_REGION);
                intent.putExtra(EXTRA_BEACON_REGION, (Parcelable) region);
                context.sendBroadcast(intent);
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d(LOG_TAG, "Region State Changed: " + state);
                Intent intent = new Intent();
                intent.setAction(ACTION_BEACON_MONITOR_DETERMINED_REGION_STATE);
                intent.putExtra(EXTRA_BEACON_REGION, (Parcelable) region);
                intent.putExtra(EXTRA_BEACON_REGION_STATE, state);
                context.sendBroadcast(intent);
            }
        });
        //Ranging Notifier
        setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                ArrayList<BeaconWrapper> beaconWrapperArrayList = new ArrayList<>(beacons.size());
                for (Beacon beacon : beacons) {
                    beaconWrapperArrayList.add(new BeaconWrapper(beacon));
                }
                Intent intent = new Intent();
                intent.setAction(ACTION_BEACON_RANGE_BEACONS);
                intent.putParcelableArrayListExtra(EXTRA_BEACONS, beaconWrapperArrayList);
                intent.putExtra(EXTRA_BEACON_REGION, (Parcelable) region);
                context.sendBroadcast(intent);
                startRangingTime = SystemClock.elapsedRealtime();
            }
        });
        setRegion(new Region(REGION_UUID, null, null, null));
        startRangingTime = -1;
        ranging = false;
        monitoring = false;
    }

    public void setRangeNotifier(RangeNotifier notifier) {
        beaconManager.addRangeNotifier(notifier);
    }

    public void setMonitorNotifier(MonitorNotifier notifier) {
        beaconManager.addMonitorNotifier(notifier);
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void startMonitoring() {
        startMonitoring(region);
    }

    public void stopMonitoring() {
        stopMonitoring(region);
    }

    public void startRanging() {
        startRanging(region);
    }

    public void stopRanging() {
        stopRanging(region);
    }

    public void startMonitoring(Region region) {
        if (permissionManager != null) {
            permissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        try {
            if (!monitoring) {
                monitoring = true;
                beaconManager.startMonitoringBeaconsInRegion(region);
                context.registerReceiver(broadcastReceiver, intentFilter);
            }
        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    public void stopMonitoring(Region region) {
        try {
            if (monitoring) {
                monitoring = false;
                beaconManager.stopMonitoringBeaconsInRegion(region);
                context.unregisterReceiver(broadcastReceiver);
            }
        } catch (RemoteException | IllegalArgumentException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    public void startRanging(Region region) {
        if (permissionManager != null) {
            permissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        try {
            if (!ranging) {
                ranging = true;
                context.registerReceiver(broadcastReceiver, intentFilter);
                beaconManager.startRangingBeaconsInRegion(region);
            }
        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    public void stopRanging(Region region) {
        try {
            if (ranging) {
                ranging = false;
                startRangingTime = -1;
                beaconManager.stopRangingBeaconsInRegion(region);
                context.unregisterReceiver(broadcastReceiver);
            }
        } catch (RemoteException | IllegalArgumentException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    public boolean isRanging() {
        return ranging;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public long getStartRangingTime() {
        return startRangingTime;
    }

    public long getRangingElapsedTime() {
        if (startRangingTime < 0) {
            return -1;
        } else {
            return SystemClock.elapsedRealtime() - startRangingTime;
        }
    }

    public void bind() {
        if (!beaconManager.isBound(beaconConsumer)) {
            beaconManager.bind(beaconConsumer);
        }
    }

    public void unbind() {
        if (beaconManager.isBound(beaconConsumer)) {
            beaconManager.unbind(beaconConsumer);
        }
    }
}