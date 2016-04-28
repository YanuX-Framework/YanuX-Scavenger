/*
 * Copyright (c) 2016 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothBase;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothDetectedDevice;
import pt.unl.fct.di.novalincs.yanux.scavenger.view.RecyclerViewSimpleListAdapter;

public class BluetoothClassicActivity extends AppCompatActivity {

    private BluetoothCollector bluetoothCollector;
    private RecyclerView bluetoothDevices;
    private RecyclerViewSimpleListAdapter<BluetoothDetectedDevice> bluetoothDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_bluetooth_classic);

        bluetoothDevices = (RecyclerView) findViewById(R.id.bluetooth_devices);
        bluetoothDevices.setLayoutManager(new LinearLayoutManager(this));
        bluetoothDevicesAdapter = new RecyclerViewSimpleListAdapter<>(new ArrayList<BluetoothDetectedDevice>());
        bluetoothDevices.setAdapter(bluetoothDevicesAdapter);
        bluetoothCollector = new BluetoothCollector(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    // When discovery finds a device
                    case BluetoothDevice.ACTION_FOUND:
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        // Get the measured RSSI from the intent
                        short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                        // Add the details into a wrapper object and display it in a list view through the adapter
                        bluetoothDevicesAdapter.getDataSet().add(new BluetoothDetectedDevice(device, rssi));
                        bluetoothDevicesAdapter.notifyDataSetChanged();
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        bluetoothDevicesAdapter.getDataSet().clear();
                        bluetoothDevicesAdapter.notifyDataSetChanged();
                        TextView bluetoothDiscoveryElapsedTime = (TextView) findViewById(R.id.bluetooth_discovery_elapsed_time);
                        bluetoothDiscoveryElapsedTime.setText(bluetoothCollector.getScanElapsedTime() + " ms");
                        bluetoothCollector.scan();
                        break;
                    default:
                        break;
                }
            }
        });
        if (!bluetoothCollector.isEnabled()) {
            BluetoothBase.enableBluetooth(this);
        }
        bluetoothCollector.scan();

        TextView bluetoothName = (TextView) findViewById(R.id.bluetooth_name);
        bluetoothName.setText(bluetoothCollector.getName());

        TextView bluetoothAddress = (TextView) findViewById(R.id.bluetooth_address);
        bluetoothAddress.setText(bluetoothCollector.getAddress());
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothCollector.scan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bluetoothCollector.cancelScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BluetoothBase.REQUEST_CODE_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, R.string.bluetooth_enabled, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.bluetooth_not_enabled, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case BluetoothBase.REQUEST_CODE_ENABLE_BLUETOOTH_DISCOVERABILITY:
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
