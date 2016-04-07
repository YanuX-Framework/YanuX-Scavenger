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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;

public class BluetoothLeCollector extends BluetoothCollector {
    public static final int SCAN_PERIOD = 10000;
    public static final String ACTION_BLUETOOTH_LE_DEVICE_FOUND = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.ACTION_BLUETOOTH_LE_DEVICE_FOUND";
    public static final String ACTION_BLUETOOTH_LE_SCAN_FINISHED = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.ACTION_BLUETOOTH_LE_SCAN_FINISHED";

    public static final String EXTRA_BLUETOOTH_LE_DEVICE = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_DEVICE";
    public static final String EXTRA_BLUETOOTH_LE_SCAN_RECORD = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_SCAN_RECORD";
    public static final String EXTRA_BLUETOOTH_LE_SCAN_ELAPSED_TIME = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_SCAN_ELAPSED_TIME";
    private final IntentFilter intentFilter;
    private final Handler handler;
    private BluetoothAdapter.LeScanCallback leScanCallback;

    public BluetoothLeCollector(final Context context, BroadcastReceiver broadcastReceiver) {
        super(context, broadcastReceiver);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BLUETOOTH_LE_DEVICE_FOUND);
        intentFilter.addAction(ACTION_BLUETOOTH_LE_SCAN_FINISHED);
        this.leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Intent intent = new Intent();
                intent.setAction(ACTION_BLUETOOTH_LE_DEVICE_FOUND);
                intent.putExtra(EXTRA_BLUETOOTH_LE_DEVICE, new BluetoothDetectedDevice(device, rssi));
                intent.putExtra(EXTRA_BLUETOOTH_LE_SCAN_RECORD, scanRecord);
                context.sendBroadcast(intent);
            }
        };
        this.handler = new Handler();
    }

    //TODO #1: Replace and/or create conditional code that allows me to drop the use of deprecated code on Android 5.0+
    @Override
    public boolean scan() {
        if (!isScanning()) {
            context.registerReceiver(broadcastReceiver, intentFilter);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    Intent intent = new Intent();
                    intent.setAction(ACTION_BLUETOOTH_LE_SCAN_FINISHED);
                    intent.putExtra(EXTRA_BLUETOOTH_LE_SCAN_ELAPSED_TIME, getScanElapsedTime());
                    context.sendBroadcast(intent);
                }
            }, SCAN_PERIOD);
            scanning = true;
            scanStartTime = SystemClock.elapsedRealtime();
            return bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            return false;
        }
    }

    @Override
    public boolean cancelScan() {
        if (isScanning()) {
            scanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
            context.unregisterReceiver(broadcastReceiver);
            return true;
        } else {
            return false;
        }
    }

}
