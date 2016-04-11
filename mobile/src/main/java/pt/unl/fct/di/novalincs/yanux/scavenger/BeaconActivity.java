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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;

import java.util.ArrayList;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacon.BeaconCollector;

public class BeaconActivity extends AppCompatActivity implements BeaconConsumer {

    private BeaconCollector beaconCollector;
    private boolean beaconCollectorReady;
    private ListView beaconList;
    private ArrayAdapter<Beacon> beaconListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        beaconCollectorReady = false;
        beaconList = (ListView) findViewById(R.id.beacon_list);
        beaconListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        beaconList.setAdapter(beaconListAdapter);
        beaconCollector = new BeaconCollector(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BeaconCollector.ACTION_BEACON_RANGE_BEACONS:
                        beaconListAdapter.clear();
                        ArrayList<Beacon> beaconsArrayList = intent.getParcelableArrayListExtra(BeaconCollector.EXTRA_BEACONS);
                        beaconListAdapter.addAll(beaconsArrayList);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconCollectorReady = true;
        beaconCollector.startRanging();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconCollector.destroy();
    }
}
