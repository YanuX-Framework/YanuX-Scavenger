/*
 * Copyright (c) 2017 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.wifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.IFileLogger;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.JsonFileLogger;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.SensorListener;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.WifiReading;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorWrapper;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiConnectionInfo;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiResult;
import pt.unl.fct.di.novalincs.yanux.scavenger.dialog.logging.LogDialogFragment;
import pt.unl.fct.di.novalincs.yanux.scavenger.view.RecyclerViewSimpleListAdapter;

public class WifiActivity extends AppCompatActivity implements LogDialogFragment.LogDialogListener {
    private static final String LOG_TAG = Constants.LOG_TAG + "_WIFI_ACTIVITY";

    private RecyclerView wifiAccessPoints;
    private RecyclerViewSimpleListAdapter<WifiResult> wifiAdapter;
    private Switch logSwitch;
    private TextView sampleCounterText;

    private PermissionManager permissionManager;
    private Preferences preferences;
    private WifiCollector wifiCollector;
    private BroadcastReceiver broadcastReceiver;
    private SensorCollector sensorCollector;
    private SensorListener sensorListener;
    private List<SensorWrapper> loggedSensors;
    private IFileLogger logger;

    private int sampleCounter;
    private int totalSamples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_wifi);

        //Wi-Fi Access Points List View
        wifiAccessPoints = (RecyclerView) findViewById(R.id.wifi_access_points);
        // use a linear layout manager
        wifiAccessPoints.setLayoutManager(new LinearLayoutManager(this));
        //Wi-Fi Access Points List View Adapter
        wifiAdapter = new RecyclerViewSimpleListAdapter<>(new ArrayList<WifiResult>());
        wifiAccessPoints.setAdapter(wifiAdapter);
        //Log switch
        logSwitch = (Switch) findViewById(R.id.log_switch);
        logSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DialogFragment logDialogFragment = new LogDialogFragment();
                    logDialogFragment.show(getSupportFragmentManager(), "WIFI_LOGGING");
                } else {
                    disableLogging();
                }
            }
        });
        //Sample Counter
        sampleCounterText = (TextView) findViewById(R.id.log_sample_counter);

        //Permission Manager
        permissionManager = new PermissionManager(this);
        permissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //Preferences
        preferences = new Preferences(this);
        //Wi-Fi Collector
        wifiCollector = new WifiCollector(this);
        //Check if Wi-Fi scanning is always available
        if (!preferences.hasAskedForWifiScanningAlwaysAvailable()
                && !wifiCollector.isScanAlwaysAvailable()) {
            WifiCollector.enableScanIsAlwaysAvailable(this);
        }
        //Wi-Fi Collector Broadcast Receiver
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView elapsedTimeText = (TextView) findViewById(R.id.wifi_elapsed_time);
                elapsedTimeText.setText(wifiCollector.getScanningElapsedTime() + " ms");
                List<WifiResult> wifiResults = wifiCollector.getScanResults();
                wifiAdapter.setDataSet(wifiResults);
                wifiAdapter.notifyDataSetChanged();
                if (logger != null && logger.isOpen()) {
                    if (sampleCounter < totalSamples) {
                        logger.log(sampleCounter, new WifiReading(wifiResults, sensorListener.getCurrentReadings(), wifiCollector.getConnectionInfo()));
                        sampleCounterText.setText(Integer.toString(sampleCounter));
                        sampleCounter++;
                    } else if (sampleCounter >= totalSamples) {
                        logSwitch.setChecked(false);
                    }
                }
                wifiCollector.scan(broadcastReceiver);
                updateConnectionInfo();
            }
        };
        sensorCollector = new SensorCollector(this);
        sensorListener = new SensorListener();
        loggedSensors = new ArrayList<>();
        if (sensorCollector.hasRotationVector()) {
            loggedSensors.add(sensorCollector.getRotationVector());
        }
        if (sensorCollector.hasOrientation()) {
            loggedSensors.add(sensorCollector.getOrientation());
        }
        if (sensorCollector.hasGravity()) {
            loggedSensors.add(sensorCollector.getGravity());
        }
        if (sensorCollector.hasPressure()) {
            loggedSensors.add(sensorCollector.getPressure());
        }
        logger = new JsonFileLogger(this);
        disableLogging();
        updateConnectionInfo();
        wifiCollector.scan(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiCollector.cancelScan(broadcastReceiver);
        disableLogging();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionManager.REQUEST_PERMISSION_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (PermissionManager.werePermissionsGranted(grantResults)) {
                    Toast.makeText(getApplicationContext(), R.string.permission_location_allowed, Toast.LENGTH_SHORT).show();
                    // Permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), R.string.permission_location_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            // other 'case' lines to check for other permissions this app might request
            case PermissionManager.REQUEST_PERMISSION_GENERIC:
                break;
            // other 'case' lines to check for other permissions this app might request
            case PermissionManager.REQUEST_MULTIPLE_PERMISSIONS:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WifiCollector.REQUEST_CODE_SCAN_ALWAYS_AVAILABLE:
                preferences.setHasAskedForWifiScanningAlwaysAvailable(true);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, R.string.wifi_scan_always_available_enabled, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.wifi_scan_always_available_not_enabled, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void updateConnectionInfo() {
        WifiConnectionInfo wifiConnectionInfo = wifiCollector.getConnectionInfo();
        TextView wifiConnectionInfoView = (TextView) findViewById(R.id.wifi_connection_info);
        wifiConnectionInfoView.setText(wifiConnectionInfo.getSsid() + " [" + wifiConnectionInfo.getBssid() + "]\nRSSI: " + wifiConnectionInfo.getRssi());
    }

    @Override
    public void onDialogPositiveClick(LogDialogFragment dialog) {
        disableLogging();
        enableLogging(dialog.getLogName(), dialog.getSamples());
    }

    @Override
    public void onDialogNegativeClick(LogDialogFragment dialog) {
        disableLogging();
        dialog.getDialog().cancel();
    }

    private void enableLogging(String logName, int numberOfSamplesToLog) {
        try {
            logger.setFilename(logName + ".json");
            logger.open();
            this.totalSamples = numberOfSamplesToLog;
            sampleCounter = 0;
            for (SensorWrapper sensor : loggedSensors) {
                sensor.registerListener(sensorListener);
            }
            findViewById(R.id.log_sample_counter_label).setVisibility(View.VISIBLE);
            sampleCounterText.setVisibility(View.VISIBLE);
            sampleCounterText.setText(Integer.toString(sampleCounter));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    private void disableLogging() {
        if (logger.isOpen()) {
            try {
                logger.close();
                totalSamples = 0;
                sampleCounter = 0;
                for (SensorWrapper sensor : loggedSensors) {
                    sensor.unregisterListener(sensorListener);
                }
                findViewById(R.id.log_sample_counter_label).setVisibility(View.GONE);
                sampleCounterText.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
    }
}