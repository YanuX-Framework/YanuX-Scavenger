/*
 * Copyright (c) 2016 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.ILogger;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.JsonLogger;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiConnectionInfo;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiResult;

public class WifiActivity extends AppCompatActivity implements LogDialogFragment.LogDialogListerner {
    private PermissionManager permissionManager;
    private WifiCollector wifiCollector;
    private Preferences preferences;
    private ListView wifiAccessPoints;
    private ArrayAdapter<WifiResult> wifiAccessPointsAdapter;
    private BroadcastReceiver broadcastReceiver;
    private int numberOfSamplesToLog;
    private ILogger logger;
    private ToggleButton logButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        permissionManager = new PermissionManager(this);
        if (Constants.API_LEVEL >= Build.VERSION_CODES.M) {
            permissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        preferences = new Preferences(this);
        wifiCollector = new WifiCollector(this);
        if (!preferences.hasAskedForWifiScanningAlwaysAvailable()
                && !wifiCollector.isScanAlwaysAvailable()) {
            WifiCollector.enableScanIsAlwaysAvailable(this);
        }
        logButton = (ToggleButton) findViewById(R.id.wifi_log_button);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment logDialogFragment = new LogDialogFragment();
                logDialogFragment.show(getSupportFragmentManager(), "WIFI_LOGGING");
            }
        });
        disableLogging();
        wifiAccessPoints = (ListView) findViewById(R.id.wifi_access_points);
        wifiAccessPointsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        wifiAccessPoints.setAdapter(wifiAccessPointsAdapter);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiAccessPointsAdapter.clear();
                List<WifiResult> wifiResults = wifiCollector.getScanResults();
                wifiAccessPointsAdapter.addAll(wifiResults);
                if (logger != null && logger.isOpen()) {
                    if (numberOfSamplesToLog > 0) {
                        for (WifiResult wifiResult : wifiResults) {
                            logger.log(wifiResult);
                        }
                        numberOfSamplesToLog--;
                    } else {
                        disableLogging();
                    }
                }
                wifiCollector.scan(broadcastReceiver);
                updateConnectionInfo();
            }
        };
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
                    Toast.makeText(getApplicationContext(), R.string.permission_location_allowed, Toast.LENGTH_SHORT).show();
                }
                break;
            // other 'case' lines to check for other permissions this app might request
            case PermissionManager.REQUEST_PERMISSION_GENERIC:
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
        String wifiConnectionInfoText = "";
        wifiConnectionInfoText += "SSID: " + wifiConnectionInfo.getSsid() + "\n";
        wifiConnectionInfoText += "BSSID: " + wifiConnectionInfo.getBssid() + "\n";
        wifiConnectionInfoText += "IP Address: " + wifiConnectionInfo.getWifiIpAdress().getHostAddress() + "\n";
        wifiConnectionInfoText += "RSSI: " + wifiConnectionInfo.getRssi() + "\n";
        wifiConnectionInfoText += "Link Speed: " + wifiConnectionInfo.getLinkSpeed() + " " + WifiInfo.LINK_SPEED_UNITS + "\n";
        wifiConnectionInfoView.setText(wifiConnectionInfoText);
    }

    @Override
    public void onDialogPositiveClick(LogDialogFragment dialog) {
        disableLogging();
        enableLogging(dialog.getLogName(), dialog.getNumberOfSamples());
    }

    @Override
    public void onDialogNegativeClick(LogDialogFragment dialog) {
        disableLogging();
        dialog.getDialog().cancel();
    }

    private void enableLogging(String logName, int numberOfSamplesToLog) {
        try {
            logger = new JsonLogger(logName + ".json");
            logger.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.numberOfSamplesToLog = numberOfSamplesToLog;
        logButton.setChecked(true);
    }

    private void disableLogging() {
        if (logger != null && logger.isOpen()) {
            try {
                logger.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        numberOfSamplesToLog = 0;
        logButton.setChecked(false);
    }
}