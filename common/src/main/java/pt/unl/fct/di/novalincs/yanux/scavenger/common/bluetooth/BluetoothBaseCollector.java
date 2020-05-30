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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BluetoothBaseCollector {
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 200;
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH_DISCOVERABILITY = 201;
    public static final int BLUETOOTH_DISCOVERABILITY_TIME = 3600;
    protected final BluetoothAdapter bluetoothAdapter;
    protected boolean scanning;

    public BluetoothBaseCollector() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static void enableBluetooth(AppCompatActivity activity) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
    }

    public static void enableBluetoothDiscoverability(AppCompatActivity activity) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BLUETOOTH_DISCOVERABILITY_TIME);
        activity.startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH_DISCOVERABILITY);
    }

    public boolean isBluetoothSupported() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return isBluetoothSupported() && bluetoothAdapter.isEnabled();
    }

    public boolean isScanning() {
        return scanning;
    }

    public boolean scan() throws BluetoothException {
        if (!isBluetoothSupported()) {
            throw new BluetoothNotSupportedException("Your device does not support Bluetooth.");
        }
        return true;
    }

    public boolean cancelScan() throws BluetoothException {
        if (!isBluetoothSupported()) {
            throw new BluetoothNotSupportedException("Your device does not support Bluetooth.");
        }
        return true;
    }
}
