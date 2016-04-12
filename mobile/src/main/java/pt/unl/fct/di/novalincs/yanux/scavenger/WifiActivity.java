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
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class WifiActivity extends AppCompatActivity {
    private PermissionManager permissionManager;
    private WifiCollector wifiCollector;
    private Preferences preferences;
    private ListView wifiAccessPoints;
    private ArrayAdapter<WifiResult> wifiAccessPointsAdapter;
    private BroadcastReceiver broadcastReceiver;
    private ILogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        permissionManager = new PermissionManager(this);
        if (Constants.API_LEVEL >= Build.VERSION_CODES.M) {
            permissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        try {
            logger = new JsonLogger("wifi-activity-incremental");
        } catch (IOException e) {
            e.printStackTrace();
        }

        wifiAccessPoints = (ListView) findViewById(R.id.wifi_access_points);
        wifiAccessPointsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        wifiAccessPoints.setAdapter(wifiAccessPointsAdapter);
        wifiCollector = new WifiCollector(this);
        preferences = new Preferences(this);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiAccessPointsAdapter.clear();
                List<WifiResult> wifiResults = wifiCollector.getScanResults();
                wifiAccessPointsAdapter.addAll(wifiResults);
                if (logger != null) {
                    for (WifiResult wifiResult : wifiResults) {
                        logger.log(wifiResult);
                    }
                }
                wifiCollector.scan(broadcastReceiver);
                updateConnectionInfo();
            }
        };
        if (!preferences.hasAskedForWifiScanningAlwaysAvailable()
                && !wifiCollector.isScanAlwaysAvailable()) {
            WifiCollector.enableScanIsAlwaysAvailable(this);
        }
        updateConnectionInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (logger != null) {
            try {
                logger.open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        wifiCollector.scan(broadcastReceiver);
        updateConnectionInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (logger != null) {
            try {
                logger.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        wifiCollector.cancelScan(broadcastReceiver);
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

        //wifiConnectionInfoText += "Network ID: " + wifiConnectionInfo.getNetworkId() + "\n";
        //wifiConnectionInfoText += "MAC Address: " + wifiConnectionInfo.getMacAddress() + "\n";
        //wifiConnectionInfoText += "Hidden SSID: " + wifiConnectionInfo.isSsidHidden() + "\n";
        //wifiConnectionInfoText += "Supplicant State: " + wifiConnectionInfo.getSupplicantState() + "\n";
        //wifiConnectionInfoText += "Detailed State: " + wifiConnectionInfo.getDetailedState() + "\n";

        /* DHCP Information */
        //wifiConnectionInfoText += "IP Address: " + wifiConnectionInfo.getIpAdress().getHostAddress() + "\n";
        //wifiConnectionInfoText += "Subnet Mask: " + wifiConnectionInfo.getNetmask().getHostAddress() + "\n";
        //wifiConnectionInfoText += "Gateway: " + wifiConnectionInfo.getGateway().getHostAddress() + "\n";
        //wifiConnectionInfoText += "DHCP Server: " + wifiConnectionInfo.getDns1().getHostAddress() + "\n";
        //wifiConnectionInfoText += "DNS 1: " + wifiConnectionInfo.getDns1().getHostAddress() + "\n";
        //wifiConnectionInfoText += "DNS 2: " + wifiConnectionInfo.getDns2().getHostAddress() + "\n";
        //wifiConnectionInfoText += "Lease Duration: " + wifiConnectionInfo.getLeaseDuration() + "\n";

        wifiConnectionInfoView.setText(wifiConnectionInfoText);
    }
}