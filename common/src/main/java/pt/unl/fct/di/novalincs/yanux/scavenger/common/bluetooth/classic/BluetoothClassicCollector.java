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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.classic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothCollector;

public class BluetoothClassicCollector extends BluetoothCollector {
    public BluetoothClassicCollector(Context context) {
        super(context);
    }

    @Override
    public boolean startDiscovery() {
        discoveryStartTime = System.nanoTime();
        return bluetoothAdapter.startDiscovery();
    }

    @Override
    public boolean startDiscovery(BroadcastReceiver broadcastReceiver) {
        if (startDiscovery()) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(broadcastReceiver, intentFilter); // Don't forget to unregister during onDestroy
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean cancelDiscovery() {
        return bluetoothAdapter.cancelDiscovery();
    }

    @Override
    public boolean cancelDiscovery(BroadcastReceiver broadcastReceiver) {
        if (cancelDiscovery()) {
            context.unregisterReceiver(broadcastReceiver);
            return true;
        } else {
            context.unregisterReceiver(broadcastReceiver);
            return false;
        }
    }
}
