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

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.CyclicTriggerEventListener;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorWrapper;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class SensorsActivity extends AppCompatActivity implements OnItemSelectedListener, SensorEventListener {
    private static final String LOG_TAG = Constants.LOG_TAG + "_SENSORS_ACTIVITY";

    private SensorCollector sensorCollector;
    private SensorWrapper selectedSensor;
    private CyclicTriggerEventListener triggerEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sensors);
        sensorCollector = new SensorCollector(this);
        triggerEventListener = new CyclicTriggerEventListener(sensorCollector.getSensorManager()) {
            @Override
            public void onTrigger(TriggerEvent event) {
                super.onTrigger(event);
                fillTimestamp(event.timestamp);
                TextView sensorValues = SensorsActivity.this.findViewById(R.id.sensor_values);
                sensorValues.setText(SensorsActivity.this.getString(R.string.sensor_significant_motion_detected) + ": " + cycle);
            }
        };
        Spinner selectSensorSpinner = findViewById(R.id.select_sensor);
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
        TextView sensorName = findViewById(R.id.sensor_name);
        sensorName.setText(selectedSensor.getName());

        TextView sensorVendor = findViewById(R.id.sensor_vendor);
        sensorVendor.setText(selectedSensor.getVendor());

        TextView sensorVersion = findViewById(R.id.sensor_version);
        sensorVersion.setText(Integer.toString(selectedSensor.getVersion()));

        TextView sensorMaxRange = findViewById(R.id.sensor_max_range);
        sensorMaxRange.setText(Float.toString(selectedSensor.getMaximumRange()));

        TextView sensorResolution = findViewById(R.id.sensor_resolution);
        sensorResolution.setText(Float.toString(selectedSensor.getResolution()));

        TextView sensorMinDelay = findViewById(R.id.sensor_min_delay);
        sensorMinDelay.setText(Integer.toString(selectedSensor.getMinDelay()));

        TextView sensorPower = findViewById(R.id.sensor_power);
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
        fillValues(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        fillAccuracy(accuracy);
    }

    private void fillAccuracy(int accuracy) {
        TextView sensorAccuracy = findViewById(R.id.sensor_accuracy);
        sensorAccuracy.setText(Integer.toString(accuracy));
    }

    private void fillValues(SensorEvent event) {
        TextView sensorValues = findViewById(R.id.sensor_values);
        String valuesText = "";
        float[] values;
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR
                || event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR
                || event.sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);
            valuesText += printSensorValues(event.values);
            valuesText += "Rotation Matrix:\n";
            valuesText += printSensorValues(rotationMatrix);
            valuesText += "Orientation: \n";
            valuesText += printSensorValues(orientation, true);
        } else {
            valuesText += printSensorValues(event.values);
        }
        sensorValues.setText(valuesText);
    }

    private String printSensorValues(float[] values) {
        return printSensorValues(values, false);
    }

    private String printSensorValues(float[] values, boolean convertRadToDeg) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String valuesText = "";
        for (int i = 0; i < values.length; i++) {
            double value;
            if (convertRadToDeg) {
                value = values[i] * 180.0 / Math.PI;
            } else {
                value = values[i];
            }
            valuesText += "[" + i + "] => " + decimalFormat.format(value) + "\n";
        }
        return valuesText;
    }

    private void fillTimestamp(long timestamp) {
        TextView sensorTimestamp = findViewById(R.id.sensor_timestamp);
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
        TextView sensorName = findViewById(R.id.sensor_name);
        sensorName.setText("");

        TextView sensorVendor = findViewById(R.id.sensor_vendor);
        sensorVendor.setText("");

        TextView sensorVersion = findViewById(R.id.sensor_version);
        sensorVersion.setText("");

        TextView sensorMaxRange = findViewById(R.id.sensor_max_range);
        sensorMaxRange.setText("");

        TextView sensorResolution = findViewById(R.id.sensor_resolution);
        sensorResolution.setText("");

        TextView sensorMinDelay = findViewById(R.id.sensor_min_delay);
        sensorMinDelay.setText("");

        TextView sensorPower = findViewById(R.id.sensor_power);
        sensorPower.setText("");

        TextView sensorValues = findViewById(R.id.sensor_values);
        sensorValues.setText("");

        TextView sensorAccuracy = findViewById(R.id.sensor_accuracy);
        sensorAccuracy.setText("");

        TextView sensorTimestamp = findViewById(R.id.sensor_timestamp);
        sensorTimestamp.setText("");
    }
}
