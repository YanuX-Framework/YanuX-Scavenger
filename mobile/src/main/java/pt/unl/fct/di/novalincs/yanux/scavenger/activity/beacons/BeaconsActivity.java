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

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.beacons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;

import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.view.RecyclerViewSimpleListAdapter;

public class BeaconsActivity extends AppCompatActivity implements BeaconConsumer {
    private static final String LOG_TAG = Constants.LOG_TAG + "_BEACONS_ACTIVITY";

    private BeaconCollector beaconCollector;
    private RecyclerView beaconList;
    private RecyclerViewSimpleListAdapter<Beacon> beaconListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_beacons);
        beaconList = findViewById(R.id.beacon_list);
        beaconList.setLayoutManager(new LinearLayoutManager(this));
        beaconListAdapter = new RecyclerViewSimpleListAdapter<>(new ArrayList<Beacon>());
        beaconList.setAdapter(beaconListAdapter);
        beaconCollector = new BeaconCollector(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BeaconCollector.ACTION_BEACON_RANGE_BEACONS:
                        List<Beacon> beaconsArrayList = intent.getParcelableArrayListExtra(BeaconCollector.EXTRA_BEACONS);
                        beaconListAdapter.setDataSet(beaconsArrayList);
                        beaconListAdapter.notifyDataSetChanged();
                        TextView rangingElapsedTimeText = findViewById(R.id.beacon_ranging_elapsed_time);
                        rangingElapsedTimeText.setText(beaconCollector.getRangingElapsedTime() + " ms");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconCollector.startRanging();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconCollector.unbind();
    }
}
