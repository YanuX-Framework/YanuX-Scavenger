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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;

public class BluetoothCollector extends BluetoothBase implements IBluetoothCollector {

    protected final Context context;
    protected final BluetoothAdapter bluetoothAdapter;
    protected BroadcastReceiver broadcastReceiver;
    protected boolean scanning;
    protected long scanStartTime;
    private PermissionManager permissionManager;

    public BluetoothCollector(Context context, BroadcastReceiver broadcastReceiver) {
        this.context = context;
        this.broadcastReceiver = broadcastReceiver;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (context instanceof AppCompatActivity) {
            permissionManager = new PermissionManager((AppCompatActivity) context);
        }
        scanning = false;
    }

    public boolean scan() {
        if (permissionManager != null) {
            permissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (bluetoothAdapter.startDiscovery()) {
            scanning = true;
            scanStartTime = SystemClock.elapsedRealtime();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(broadcastReceiver, intentFilter); // Don't forget to unregister during onDestroy
            return true;
        } else {
            return false;
        }
    }

    public boolean cancelScan() {
        scanning = false;
        context.unregisterReceiver(broadcastReceiver);
        return bluetoothAdapter.cancelDiscovery();
    }

    public long getScanElapsedTime() {
        return SystemClock.elapsedRealtime() - scanStartTime;
    }

    public String getName() {
        return bluetoothAdapter.getName();
    }

    public String getAddress() {
        return bluetoothAdapter.getAddress();
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public boolean isScanning() {
        return scanning;
    }
}
