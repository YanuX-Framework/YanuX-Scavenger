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

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.TriggerEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.CyclicTriggerEventListener;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorWrapper;

public class SensorsActivity extends AppCompatActivity implements OnItemSelectedListener, SensorEventListener {
    private SensorCollector sensorCollector;
    private SensorWrapper selectedSensor;
    private CyclicTriggerEventListener triggerEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        sensorCollector = new SensorCollector(this);
        triggerEventListener = new CyclicTriggerEventListener(sensorCollector.getSensorManager()) {
            @Override
            public void onTrigger(TriggerEvent event) {
                super.onTrigger(event);
                fillTimestamp(event.timestamp);
                TextView sensorValues = (TextView) SensorsActivity.this.findViewById(R.id.sensor_values);
                sensorValues.setText(SensorsActivity.this.getString(R.string.sensor_significant_motion_detected) + ":" + cycle);
            }
        };

        Spinner selectSensorSpinner = (Spinner) findViewById(R.id.select_sensor);
        ArrayAdapter<SensorWrapper> selectSensorSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        selectSensorSpinnerAdapter.addAll(sensorCollector.getSensors());
        selectSensorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectSensorSpinner.setAdapter(selectSensorSpinnerAdapter);
        selectSensorSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensorListener();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        unregisterSensorListener();
        clear();

        selectedSensor = (SensorWrapper) parent.getItemAtPosition(position);
        TextView sensorName = (TextView) findViewById(R.id.sensor_name);
        sensorName.setText(selectedSensor.getName());

        TextView sensorVendor = (TextView) findViewById(R.id.sensor_vendor);
        sensorVendor.setText(selectedSensor.getVendor());

        TextView sensorVersion = (TextView) findViewById(R.id.sensor_version);
        sensorVersion.setText(Integer.toString(selectedSensor.getVersion()));

        TextView sensorMaxRange = (TextView) findViewById(R.id.sensor_max_range);
        sensorMaxRange.setText(Float.toString(selectedSensor.getMaximumRange()));

        TextView sensorResolution = (TextView) findViewById(R.id.sensor_resolution);
        sensorResolution.setText(Float.toString(selectedSensor.getResolution()));

        TextView sensorMinDelay = (TextView) findViewById(R.id.sensor_min_delay);
        sensorMinDelay.setText(Integer.toString(selectedSensor.getMinDelay()));

        TextView sensorPower = (TextView) findViewById(R.id.sensor_power);
        sensorPower.setText(Float.toString(selectedSensor.getPower()));

        registerSensorListener();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        unregisterSensorListener();
        clear();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        fillTimestamp(event.timestamp);
        fillAccuracy(event.accuracy);
        fillValues(event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        fillAccuracy(accuracy);
    }

    private void fillAccuracy(int accuracy) {
        TextView sensorAccuracy = (TextView) findViewById(R.id.sensor_accuracy);
        sensorAccuracy.setText(Integer.toString(accuracy));
    }

    private void fillValues(float[] values) {
        TextView sensorValues = (TextView) findViewById(R.id.sensor_values);
        String valuesText = new String();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (int i = 0; i < values.length; i++) {
            valuesText += "[" + i + "] => " + decimalFormat.format(values[i]) + "\n";
        }
        sensorValues.setText(valuesText);
    }

    private void fillTimestamp(long timestamp) {
        TextView sensorTimestamp = (TextView) findViewById(R.id.sensor_timestamp);
        sensorTimestamp.setText(Long.toString(timestamp));
    }

    private void registerSensorListener() {
        if (selectedSensor != null) {
            if (selectedSensor.getType() != Sensor.TYPE_SIGNIFICANT_MOTION) {
                selectedSensor.registerListener(this);
            } else {
                selectedSensor.registerTriggerListener(triggerEventListener);
            }
        }
    }

    private void unregisterSensorListener() {
        if (selectedSensor != null) {
            if (selectedSensor.getType() != Sensor.TYPE_SIGNIFICANT_MOTION) {
                selectedSensor.unregisterListener(this);
            } else {
                selectedSensor.unregisterTriggerListener(triggerEventListener);
            }
        }
    }

    private void clear() {
        TextView sensorName = (TextView) findViewById(R.id.sensor_name);
        sensorName.setText(new String());

        TextView sensorVendor = (TextView) findViewById(R.id.sensor_vendor);
        sensorVendor.setText(new String());

        TextView sensorVersion = (TextView) findViewById(R.id.sensor_version);
        sensorVersion.setText(new String());

        TextView sensorMaxRange = (TextView) findViewById(R.id.sensor_max_range);
        sensorMaxRange.setText(new String());

        TextView sensorResolution = (TextView) findViewById(R.id.sensor_resolution);
        sensorResolution.setText(new String());

        TextView sensorMinDelay = (TextView) findViewById(R.id.sensor_min_delay);
        sensorMinDelay.setText(new String());

        TextView sensorPower = (TextView) findViewById(R.id.sensor_power);
        sensorPower.setText(new String());

        TextView sensorValues = (TextView) findViewById(R.id.sensor_values);
        sensorValues.setText(new String());

        TextView sensorAccuracy = (TextView) findViewById(R.id.sensor_accuracy);
        sensorAccuracy.setText(new String());

        TextView sensorTimestamp = (TextView) findViewById(R.id.sensor_timestamp);
        sensorTimestamp.setText(new String());
    }
}
