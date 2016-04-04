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
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;

public class SensorWrapper {
    private final SensorManager sensorManager;
    private final String description;
    private final Sensor sensor;

    public SensorWrapper(SensorManager sensorManager, String description, Sensor sensor) {
        this.sensorManager = sensorManager;
        this.description = description;
        this.sensor = sensor;
    }

    public SensorWrapper(SensorManager sensorManager, Sensor sensor) {
        this(sensorManager, sensor.getName(), sensor);
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasSensor() {
        return sensor != null;
    }

    public boolean registerListener(SensorEventListener listener) {
        return sensorManager.registerListener(listener, sensor, SensorCollector.SENSOR_DELAY);
    }

    public boolean registerListener(SensorEventListener listener, int delay) {
        return sensorManager.registerListener(listener, sensor, delay);
    }

    public void unregisterListener(SensorEventListener listener) {
        sensorManager.unregisterListener(listener, sensor);
    }

    public boolean registerTriggerListener(TriggerEventListener listener) {
        return sensorManager.requestTriggerSensor(listener, sensor);
    }

    public boolean unregisterTriggerListener(TriggerEventListener listener) {
        return sensorManager.cancelTriggerSensor(listener, sensor);
    }

    public String getName() {
        return sensor.getName();
    }

    public String getVendor() {
        return sensor.getVendor();
    }

    public int getVersion() {
        return sensor.getVersion();
    }

    public float getMaximumRange() {
        return sensor.getMaximumRange();
    }

    public float getResolution() {
        return sensor.getResolution();
    }

    public int getMinDelay() {
        return sensor.getMinDelay();
    }

    public float getPower() {
        return sensor.getPower();
    }

    public int getType() {
        return sensor.getType();
    }

    @Override
    public String toString() {
        return description;
    }
}
