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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class WifiCollector {
    public static final int REQUEST_CODE_SCAN_ALWAYS_AVAILABLE = 100;
    public static final String ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE = WifiManager.ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE;
    private final Context context;
    private final WifiManager wifiManager;
    private PermissionManager permissionManager;

    public WifiCollector(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (context instanceof Activity) {
            permissionManager = new PermissionManager((Activity) context);
        }
    }

    public static void enableScanIsAlwaysAvailable(Activity activity) {
        activity.startActivityForResult(new Intent(ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE), REQUEST_CODE_SCAN_ALWAYS_AVAILABLE);
    }

    public WifiConnectionInfo getConnectionInfo() {
        return new WifiConnectionInfo(wifiManager);
    }

    public void scan(BroadcastReceiver broadcastReceiver) {
        if (permissionManager != null && Constants.API_LEVEL >= Build.VERSION_CODES.M) {
            permissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!wifiManager.isScanAlwaysAvailable()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void cancelScan(BroadcastReceiver broadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver);
    }

    public boolean isScanAlwaysAvailable() {
        return wifiManager.isScanAlwaysAvailable();
    }

    public List<WifiResult> getScanResults() {
        List<ScanResult> scanResults = wifiManager.getScanResults();
        List<WifiResult> wifiResults = new ArrayList<>(wifiManager.getScanResults().size());
        for (ScanResult scanResult : scanResults) {
            wifiResults.add(new WifiResult(scanResult));
        }
        return wifiResults;
    }
}
