/*
 * Copyright (c) 2018 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothBase;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothDetectedDevice;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth.BluetoothLeCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.view.RecyclerViewSimpleListAdapter;

public class BluetoothLeActivity extends AppCompatActivity {
    private static final String LOG_TAG = Constants.LOG_TAG + "_BLUETOOTH_LE_ACTIVITY";

    private BluetoothLeCollector bluetoothLeCollector;
    private RecyclerView bluetoothLeDevices;
    private RecyclerViewSimpleListAdapter<BluetoothDetectedDevice> bluetoothLeDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_bluetooth_le);

        bluetoothLeDevices = findViewById(R.id.bluetooth_le_devices);
        bluetoothLeDevices.setLayoutManager(new LinearLayoutManager(this));
        /**
         * TODO:
         * I should probably replace this RecyclerViewSimpleListAdapter, which uses a List<E> to
         * store data, by something that uses a Map<K,V>. This way, I avoid having to constantly
         * remove and then add a device to the list. Besides, being more efficient, it should also
         * preserve the display order more or less unchanged. Right now, if you have more than one
         * beacon nearby, the list just shifts intermittently between all the devices around you.
         */
        bluetoothLeDevicesAdapter = new RecyclerViewSimpleListAdapter<>(new ArrayList<BluetoothDetectedDevice>());
        bluetoothLeDevices.setAdapter(bluetoothLeDevicesAdapter);

        bluetoothLeCollector = new BluetoothLeCollector(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BluetoothLeCollector.ACTION_BLUETOOTH_LE_DEVICE_FOUND:
                        BluetoothDetectedDevice bluetoothDetectedDevice = intent.getParcelableExtra(BluetoothLeCollector.EXTRA_BLUETOOTH_LE_DEVICE);
                        bluetoothLeDevicesAdapter.getDataSet().remove(bluetoothDetectedDevice);
                        bluetoothLeDevicesAdapter.getDataSet().add(bluetoothDetectedDevice);
                        bluetoothLeDevicesAdapter.notifyDataSetChanged();
                        break;
                    case BluetoothLeCollector.ACTION_BLUETOOTH_LE_SCAN_FINISHED:
                        bluetoothLeDevicesAdapter.getDataSet().clear();
                        bluetoothLeDevicesAdapter.notifyDataSetChanged();
                        TextView bluetoothDiscoveryElapsedTime = findViewById(R.id.bluetooth_le_discovery_elapsed_time);
                        bluetoothDiscoveryElapsedTime.setText(intent.getLongExtra(BluetoothLeCollector.EXTRA_BLUETOOTH_LE_SCAN_ELAPSED_TIME, 0) + " ms");
                        bluetoothLeCollector.scan();
                        break;
                    default:
                        break;

                }
            }
        });

        if (!bluetoothLeCollector.isEnabled()) {
            BluetoothBase.enableBluetooth(this);
        }

        TextView bluetoothName = findViewById(R.id.bluetooth_le_name);
        bluetoothName.setText(bluetoothLeCollector.getName());

        TextView bluetoothAddress = findViewById(R.id.bluetooth_le_address);
        bluetoothAddress.setText(bluetoothLeCollector.getAddress());

    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothLeCollector.scan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bluetoothLeCollector.cancelScan();
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