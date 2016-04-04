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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.text.DecimalFormat;

/**
 * Created by Pedro on 20/03/2016.
 */
public class SensorCollectorListener implements SensorEventListener {
    private final SensorCollector sensorCollector;

    public SensorCollectorListener(SensorCollector sensorCollector) {
        this.sensorCollector = sensorCollector;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String output = event.sensor.getName();
        DecimalFormat formatter = new DecimalFormat("#0.00");
        for (int i = 0; i < event.values.length; i++) {
            output += i + ": " + formatter.format(event.values[i]);
            if (i < event.values.length - 1) {
                output += " ";
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
