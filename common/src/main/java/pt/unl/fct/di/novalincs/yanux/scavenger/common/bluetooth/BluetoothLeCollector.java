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
import android.content.Context;
import android.os.Handler;

public class BluetoothLeCollector extends BluetoothCollector {
    public static final int BLUETOOTH_LE_SCAN_PERIOD = 10000;
    public static final int BLUETOOTH_LE_SCAN_FINISHED_MESSAGE_CODE = 300;

    private final Handler leHandler;
    private boolean leScanning;
    private long leScanStartTime;

    public BluetoothLeCollector(Context context) {
        super(context);
        this.leHandler = new Handler();
        this.leScanning = false;
    }

    public BluetoothLeCollector(Context context, Handler leHandler) {
        super(context);
        this.leHandler = leHandler;
        this.leScanning = false;
    }

    //TODO #1: Replace and/or create conditional code that allows me to drop the use of deprecated code on Android 5.0+
    //TODO #2: I think I may be able to replace the BluetoothAdapter.LeScanCallback with a BroadcastReceiver, of course that I still have to receive the broadcasts locally (i.e., on this class) before I can broadcast them through Android's Intents. That should also allow me to remove the Handler from the constructor and to consolidate in order to @Override the superclass methods.
    public boolean startLeDiscovery(final BluetoothAdapter.LeScanCallback leScanCallback) {
        if (!isLeScanning()) {
            leHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    leScanning = false;
                    leHandler.sendEmptyMessage(BLUETOOTH_LE_SCAN_FINISHED_MESSAGE_CODE);
                }
            }, BLUETOOTH_LE_SCAN_PERIOD);
            leScanning = true;
            leScanStartTime = System.nanoTime();
            return bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            return false;
        }
    }

    public boolean isLeScanning() {
        return leScanning;
    }

    public void cancelLeDiscovery(final BluetoothAdapter.LeScanCallback leScanCallback) {
        leScanning = false;
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    public long getLeScanElapsedTimeNano() {
        return System.nanoTime() - leScanStartTime;
    }

    public long getLeScanElapsedTimeMicro() {
        return getLeScanElapsedTimeNano() / 1000;
    }

    public long getLeScanElapsedTimeMilli() {
        return getLeScanElapsedTimeMicro() / 1000;
    }

    public long getLeScanElapsedTimeSec() {
        return getLeScanElapsedTimeMilli() / 1000;
    }
}
