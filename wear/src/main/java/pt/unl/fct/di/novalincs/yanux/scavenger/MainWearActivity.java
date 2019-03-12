/*
 * Copyright (c) 2019 Pedro Albuquerque Santos.
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

import android.os.Bundle;
import android.widget.TextView;

import java.util.Collection;

import androidx.appcompat.app.AppCompatActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorWrapper;

public class MainWearActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);
        mTextView = findViewById(R.id.text);
        SensorCollector sensorCollector = new SensorCollector(MainWearActivity.this);
        Collection<SensorWrapper> sensors = sensorCollector.getAllSensors();
        String text = "";
        for (SensorWrapper sensor : sensors) {
            text += ">> [" + sensor.getDescription() + " | " + sensor.getName() + " | " + sensor.getVendor() + "]\n";
        }
        mTextView.setText(mTextView.getText() + text);
    }
}
