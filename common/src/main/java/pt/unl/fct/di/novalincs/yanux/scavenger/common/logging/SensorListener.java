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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class SensorListener implements SensorEventListener {
    private static final String LOG_TAG = Constants.LOG_TAG + "_SENSOR_LISTENER";
    Map<Sensor, SensorReading> sensorReadings;

    public SensorListener() {
        sensorReadings = new HashMap<>();
    }

    public List<SensorReading> getCurrentReadings() {
        List<SensorReading> currentReadings = new ArrayList<>(sensorReadings.size());
        for (SensorReading sensorSample : sensorReadings.values()) {
            currentReadings.add(sensorSample);
        }
        return currentReadings;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(LOG_TAG, "Sensor Changed: " + event.sensor.getName());
        sensorReadings.put(event.sensor, new SensorReading(event));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(LOG_TAG, "Sensor: " + sensor.getName() + " Accuracy Changed: " + accuracy);
    }
}
