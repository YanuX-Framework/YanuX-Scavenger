/*
 * Copyright (c) 2016 Pedro Albuquerque Santos
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.store.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiResult;

public class WifiActivity extends AppCompatActivity {
    private WifiCollector wifiCollector;
    private Preferences preferences;
    private ListView wifiAccessPointList;
    private ArrayAdapter<WifiResult> wifiAccessPointListAdapter;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        wifiAccessPointList = (ListView) findViewById(R.id.wifi_access_points);
        wifiAccessPointListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        wifiAccessPointList.setAdapter(wifiAccessPointListAdapter);
        wifiCollector = new WifiCollector(this);
        preferences = new Preferences(this);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiAccessPointListAdapter.clear();
                wifiAccessPointListAdapter.addAll(wifiCollector.getScanResults());
                wifiCollector.scan(broadcastReceiver);
                updateConnectionInfo();
                updateDhcpInfo();
            }
        };
        if (!preferences.hasAskedForWifiScanningAlwaysAvailable()
                && !wifiCollector.isScanAlwaysAvailable()) {
            startActivityForResult(new Intent(WifiCollector.ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE),
                    WifiCollector.REQUEST_CODE_SCAN_ALWAYS_AVAILABLE);
        }
        updateConnectionInfo();
        updateDhcpInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiCollector.scan(broadcastReceiver);
        updateConnectionInfo();
        updateDhcpInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        WifiInfo wifiConnectionInfo = wifiCollector.getConnectionInfo();
        TextView wifiConnectionInfoView = (TextView) findViewById(R.id.wifi_connection_info);
        String wifiConnectionInfoText = new String();
        wifiConnectionInfoText += "SSID: " + wifiConnectionInfo.getSSID() + "\n";
        wifiConnectionInfoText += "Hidden SSID: " + wifiConnectionInfo.getHiddenSSID() + "\n";
        wifiConnectionInfoText += "BSSID: " + wifiConnectionInfo.getBSSID() + "\n";
        wifiConnectionInfoText += "MAC Address: " + wifiConnectionInfo.getMacAddress() + "\n";
        wifiConnectionInfoText += "IP Address: " + WifiCollector.getIpAddress(wifiConnectionInfo.getIpAddress()) + "\n";
        wifiConnectionInfoText += "RSSI: " + wifiConnectionInfo.getRssi() + "\n";
        wifiConnectionInfoText += "Link Speed: " + wifiConnectionInfo.getLinkSpeed() + " " + WifiInfo.LINK_SPEED_UNITS + "\n";
        wifiConnectionInfoText += "Network ID: " + wifiConnectionInfo.getNetworkId() + "\n";
        wifiConnectionInfoText += "Supplicant State: " + wifiConnectionInfo.getSupplicantState() + "\n";
        wifiConnectionInfoView.setText(wifiConnectionInfoText);
    }

    private void updateDhcpInfo() {
        DhcpInfo dhcpInfo = wifiCollector.getDhcpInfo();
        TextView dhcpInfoView = (TextView) findViewById(R.id.wifi_dhcp_info);
        String dhcpInfoText = new String();
        dhcpInfoText += "IP Address: " + WifiCollector.getIpAddress(dhcpInfo.ipAddress) + "\n";
        dhcpInfoText += "Subnet Mask: " + WifiCollector.getIpAddress(dhcpInfo.netmask) + "\n";
        dhcpInfoText += "Gateway: " + WifiCollector.getIpAddress(dhcpInfo.gateway) + "\n";
        dhcpInfoText += "DHCP Server: " + WifiCollector.getIpAddress(dhcpInfo.serverAddress) + "\n";
        dhcpInfoText += "DNS 1: " + WifiCollector.getIpAddress(dhcpInfo.dns1) + "\n";
        dhcpInfoText += "DNS 2: " + WifiCollector.getIpAddress(dhcpInfo.dns2) + "\n";
        dhcpInfoText += "Lease Duration: " + dhcpInfo.leaseDuration + "\n";
        dhcpInfoView.setText(dhcpInfoText);
    }

}