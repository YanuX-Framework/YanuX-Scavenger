/*
 * Copyright (c) 2016 Pedro Albuquerque Santos
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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorWrapper;

public class SensorsActivity extends AppCompatActivity implements OnItemSelectedListener {
    private SensorCollector sensorCollector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        sensorCollector = new SensorCollector(this);

        Spinner selectSensorSpinner = (Spinner) findViewById(R.id.select_sensor);
        ArrayAdapter<SensorWrapper> sensorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        sensorsAdapter.addAll(sensorCollector.getAllSensors());
        sensorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectSensorSpinner.setAdapter(sensorsAdapter);
        selectSensorSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SensorWrapper sensor = (SensorWrapper) parent.getItemAtPosition(position);
        TextView sensorName = (TextView) findViewById(R.id.sensor_name);
        sensorName.setText(sensor.getSensor().getName());

        TextView sensorVendor = (TextView) findViewById(R.id.sensor_vendor);
        sensorVendor.setText(sensor.getSensor().getVendor());

        TextView sensorVersion = (TextView) findViewById(R.id.sensor_version);
        sensorVersion.setText(Integer.toString(sensor.getSensor().getVersion()));

        TextView sensorMaxRange = (TextView) findViewById(R.id.sensor_max_range);
        sensorMaxRange.setText(Float.toString(sensor.getSensor().getMaximumRange()));

        TextView sensorResolution = (TextView) findViewById(R.id.sensor_resolution);
        sensorResolution.setText(Float.toString(sensor.getSensor().getResolution()));

        TextView sensorMinDelay = (TextView) findViewById(R.id.sensor_min_delay);
        sensorMinDelay.setText(Integer.toString(sensor.getSensor().getMinDelay()));

        TextView sensorPower = (TextView) findViewById(R.id.sensor_power);
        sensorPower.setText(Float.toString(sensor.getSensor().getPower()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
