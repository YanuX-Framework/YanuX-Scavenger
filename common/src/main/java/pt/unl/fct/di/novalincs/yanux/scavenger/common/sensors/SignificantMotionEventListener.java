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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.widget.Toast;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.R;

public class SignificantMotionEventListener extends TriggerEventListener {
    private final Context context;
    private final SensorManager sensorManager;
    private final Sensor sensor;

    public SignificantMotionEventListener(Context context, SensorManager sensorManager, Sensor sensor) {
        this.context = context;
        this.sensorManager = sensorManager;
        this.sensor = sensor;
    }

    @Override
    public void onTrigger(TriggerEvent event) {
        if(event.sensor == sensor) {
            Toast.makeText(SignificantMotionEventListener.this.context, R.string.sensor_significant_motion_detected, Toast.LENGTH_SHORT).show();
            sensorManager.requestTriggerSensor(this, sensor);
        }
    }
}
