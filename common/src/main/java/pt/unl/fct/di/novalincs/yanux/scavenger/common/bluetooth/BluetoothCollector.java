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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Set;

public abstract class BluetoothCollector {
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 200;
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH_DISCOVERABILITY = 201;
    public static final int BLUETOOTH_DISCOVERABILITY_TIME = 3600;
    protected final Context context;
    protected final BluetoothAdapter bluetoothAdapter;
    protected long discoveryStartTime;

    public BluetoothCollector(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static void enableBluetooth(Activity activity) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
    }

    public static void enableBluetoothDiscoverability(Activity activity) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BLUETOOTH_DISCOVERABILITY_TIME);
        activity.startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH_DISCOVERABILITY);
    }

    public abstract boolean startDiscovery();

    public abstract boolean startDiscovery(BroadcastReceiver broadcastReceiver);

    public abstract boolean cancelDiscovery();

    public abstract boolean cancelDiscovery(BroadcastReceiver broadcastReceiver);

    public long getDiscoveryStartTime() {
        return discoveryStartTime;
    }

    public String getName() {
        return bluetoothAdapter.getName();
    }

    public boolean setName(String name) {
        return bluetoothAdapter.setName(name);
    }

    public String getAddress() {
        return bluetoothAdapter.getAddress();
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public boolean isDiscovering() {
        return bluetoothAdapter.isDiscovering();
    }

    public int getState() {
        return bluetoothAdapter.getState();
    }

    public int getScanMode() {
        return bluetoothAdapter.getScanMode();
    }

    public Set<BluetoothDevice> getPairedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }
}
