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

package pt.unl.fct.di.novalincs.yanux.scavenger;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import java.util.Collection;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorWrapper;

public class MainWearActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);
        // TODO: Replace the deprected WatchViewStub with BoxInsetLayout
        final WatchViewStub stub = findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = stub.findViewById(R.id.text);
                SensorCollector sensorCollector = new SensorCollector(MainWearActivity.this);
                Collection<SensorWrapper> sensors = sensorCollector.getAllSensors();
                String text = "";
                for (SensorWrapper sensor : sensors) {
                    text += ">> [" + sensor.getDescription() + " | " + sensor.getName() + " | " + sensor.getVendor() + "]\n";
                }
                mTextView.setText(mTextView.getText() + text);
            }
        });
    }
}
