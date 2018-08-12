/*
 * Copyright (c) 2017 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class BluetoothLeCollector extends BluetoothCollector implements IBluetoothCollector {
    private static final String LOG_TAG = Constants.LOG_TAG + "_BEACON_LE_COLLECTOR";
    public static final int SCAN_PERIOD = 100000;
    public static final String ACTION_BLUETOOTH_LE_DEVICE_FOUND = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.ACTION_BLUETOOTH_LE_DEVICE_FOUND";
    public static final String ACTION_BLUETOOTH_LE_BATCH_RESULTS = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.ACTION_BLUETOOTH_LE_BATCH_RESULTS";
    public static final String ACTION_BLUETOOTH_LE_SCAN_FINISHED = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.ACTION_BLUETOOTH_LE_SCAN_FINISHED";
    public static final String ACTION_BLUETOOTH_LE_SCAN_ERROR = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.ACTION_BLUETOOTH_LE_SCAN_ERROR";

    public static final String EXTRA_BLUETOOTH_LE_SCAN_RESULT = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_SCAN_RESULT";
    public static final String EXTRA_BLUETOOTH_LE_SCAN_CALLBACK_TYPE = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_SCAN_CALLBACK_TYPE";
    public static final String EXTRA_BLUETOOTH_LE_DEVICE = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_DEVICE";
    public static final String EXTRA_BLUETOOTH_LE_SCAN_RECORD = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_SCAN_RECORD";
    public static final String EXTRA_BLUETOOTH_LE_BATCH_RESULTS = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_BATCH_RESULTS";
    public static final String EXTRA_BLUETOOTH_LE_SCAN_ELAPSED_TIME = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_SCAN_ELAPSED_TIME";
    public static final String EXTRA_BLUETOOTH_LE_ERROR_CODE = "pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector.EXTRA_BLUETOOTH_LE_ERROR_CODE";

    private final IntentFilter intentFilter;
    private final Handler handler;
    private ScanCallback scanCallback;
    private PermissionManager permissionManager;

    public BluetoothLeCollector(final Context context, BroadcastReceiver broadcastReceiver) {
        super(context, broadcastReceiver);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BLUETOOTH_LE_DEVICE_FOUND);
        intentFilter.addAction(ACTION_BLUETOOTH_LE_BATCH_RESULTS);
        intentFilter.addAction(ACTION_BLUETOOTH_LE_SCAN_FINISHED);
        intentFilter.addAction(ACTION_BLUETOOTH_LE_SCAN_ERROR);
        handler = new Handler();
        initBleCallback();
        if (context instanceof Activity) {
            permissionManager = new PermissionManager((Activity) context);
        }
    }

    private void initBleCallback() {
        scanCallback = new ScanCallback() {
            /**
             * Callback when a BLE advertisement has been found.
             *
             * @param callbackType Determines how this callback was triggered. Could be one of
             *                     {@link ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
             *                     {@link ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
             *                     {@link ScanSettings#CALLBACK_TYPE_MATCH_LOST}
             * @param result       A Bluetooth LE scan result.
             */
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                Intent intent = new Intent();
                intent.setAction(ACTION_BLUETOOTH_LE_DEVICE_FOUND);
                intent.putExtra(EXTRA_BLUETOOTH_LE_SCAN_CALLBACK_TYPE, callbackType);
                intent.putExtra(EXTRA_BLUETOOTH_LE_SCAN_RESULT, result);
                intent.putExtra(EXTRA_BLUETOOTH_LE_DEVICE, new BluetoothDetectedDevice(result.getDevice(), result.getRssi()));
                intent.putExtra(EXTRA_BLUETOOTH_LE_SCAN_RECORD, result.getScanRecord().getBytes());
                Log.d(LOG_TAG,"Scan Result - Device Address: "+result.getDevice().getAddress());
                context.sendBroadcast(intent);
            }

            /**
             * Callback when batch results are delivered.
             *
             * @param results List of scan results that are previously scanned.
             */
            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Intent intent = new Intent();
                intent.setAction(ACTION_BLUETOOTH_LE_BATCH_RESULTS);
                intent.putExtra(EXTRA_BLUETOOTH_LE_BATCH_RESULTS, results.toArray());
                context.sendBroadcast(intent);
            }

            /**
             * Callback when scan could not be started.
             *
             * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
             */
            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Intent intent = new Intent();
                intent.setAction(ACTION_BLUETOOTH_LE_SCAN_ERROR);
                intent.putExtra(EXTRA_BLUETOOTH_LE_ERROR_CODE, errorCode);
                context.sendBroadcast(intent);
            }
        };
    }

    @Override
    public boolean scan() {
        if (permissionManager != null) {
            permissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!isScanning()) {
            final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            context.registerReceiver(broadcastReceiver, intentFilter);
            ///*
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(scanCallback);
                    Intent intent = new Intent();
                    intent.setAction(ACTION_BLUETOOTH_LE_SCAN_FINISHED);
                    intent.putExtra(EXTRA_BLUETOOTH_LE_SCAN_ELAPSED_TIME, getScanElapsedTime());
                    context.sendBroadcast(intent);
                }
            }, SCAN_PERIOD);
            //*/
            scanning = true;
            scanStartTime = SystemClock.elapsedRealtime();
            //The new API allows you to parametrize how the scan will be carried through the use of the ScanSettings class. Right here I'm just using the most basic startScan method which just uses the default configuration.
            final ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
            scanSettingsBuilder.setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
            scanSettingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
            scanSettingsBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
            scanSettingsBuilder.setReportDelay(0);
            bluetoothLeScanner.startScan(null, scanSettingsBuilder.build(), scanCallback);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean cancelScan() {
        if (isScanning()) {
            final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            scanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
            context.unregisterReceiver(broadcastReceiver);
            return true;
        } else {
            return false;
        }
    }
}
