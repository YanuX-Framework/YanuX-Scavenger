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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;

public class WifiCollector {
    private final Activity activity;
    private final WifiManager wifiManager;
    private final PermissionManager permissionManager;

    public WifiCollector(Activity activity) {
        this.activity = activity;
        wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        permissionManager = new PermissionManager(activity);
    }

    public void scan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        //wifiScanResultsIntentFilter = new IntentFilter();
        //wifiScanResultsIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //wifiScanResultsBroadcastReceiver = new BroadcastReceiver() {
        //    @Override
        //    public void onReceive(Context context, Intent intent) {
        //        TextView textView = (TextView) findViewById(R.id.text_wifi_list);
        //        textView.setText("Results:\n");
        //        List<ScanResult> results = wifiManager.getScanResults();
        //        for (ScanResult result : results) {
        //            textView.setText(textView.getText()
        //                    + result.SSID
        //                    + " (" + result.BSSID + "): "
        //                    + result.level
        //                    + " [ "+ result.frequency
        //                    +" ]\n");
        //        }
        //        wifiManager.startScan();
        //    }
        //};
        //registerReceiver(wifiScanResultsBroadcastReceiver, wifiScanResultsIntentFilter);
    }
}
