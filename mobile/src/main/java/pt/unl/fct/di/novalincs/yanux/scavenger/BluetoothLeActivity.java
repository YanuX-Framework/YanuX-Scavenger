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

package pt.unl.fct.di.novalincs.yanux.scavenger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothDetectedDevice;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.le.BluetoothLeCollector;

public class BluetoothLeActivity extends AppCompatActivity {

    private BluetoothLeCollector bluetoothLeCollector;
    private ListView bluetoothLeDevices;
    private ArrayAdapter<BluetoothDetectedDevice> bluetoothLeDevicesAdapter;
    private BluetoothAdapter.LeScanCallback leScanCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_le);

        bluetoothLeDevices = (ListView) findViewById(R.id.bluetooth_le_devices);
        bluetoothLeDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        bluetoothLeDevices.setAdapter(bluetoothLeDevicesAdapter);

        leScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                bluetoothLeDevicesAdapter.add(new BluetoothDetectedDevice(device, rssi));
            }
        };
        bluetoothLeCollector = new BluetoothLeCollector(this, new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case BluetoothLeCollector.BLUETOOTH_LE_SCAN_FINISHED_MESSAGE_CODE:
                        bluetoothLeDevicesAdapter.clear();
                        TextView bluetoothDiscoveryElapsedTime = (TextView) findViewById(R.id.bluetooth_le_discovery_elapsed_time);
                        bluetoothDiscoveryElapsedTime.setText(bluetoothLeCollector.getLeScanElapsedTimeMilli() + " ms");
                        bluetoothLeCollector.startLeDiscovery(leScanCallback);
                        break;
                    default:
                        break;
                }
            }
        });
        bluetoothLeCollector.startLeDiscovery(leScanCallback);

        if (!bluetoothLeCollector.isEnabled()) {
            BluetoothCollector.enableBluetooth(this);
        }

        TextView bluetoothName = (TextView) findViewById(R.id.bluetooth_le_name);
        bluetoothName.setText(bluetoothLeCollector.getName());

        TextView bluetoothAddress = (TextView) findViewById(R.id.bluetooth_le_address);
        bluetoothAddress.setText(bluetoothLeCollector.getAddress());
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothLeCollector.startLeDiscovery(leScanCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bluetoothLeCollector.cancelLeDiscovery(leScanCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BluetoothCollector.REQUEST_CODE_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, R.string.bluetooth_enabled, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.bluetooth_not_enabled, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case BluetoothCollector.REQUEST_CODE_ENABLE_BLUETOOTH_DISCOVERABILITY:
                if (resultCode != RESULT_CANCELED) {
                    Toast.makeText(this, R.string.bluetooth_discoverability_enabled, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.bluetooth_discoverability_not_enabled, Toast.LENGTH_SHORT).show();
                    finish();
                }
            default:
                break;
        }
    }
}