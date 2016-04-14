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
    protected final SensorManager sensorManager;
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

    public Sensor getSensor() {
        return sensor;
    }

    public boolean hasSensor() {
        return sensor != null;
    }

    public boolean registerListener(SensorEventListener listener, int delay) {
        return sensorManager.registerListener(listener, sensor, delay);
    }

    public boolean registerListener(SensorEventListener listener) {
        return registerListener(listener, SensorCollector.SENSOR_DELAY);
    }

    public void unregisterListener(SensorEventListener listener) {
        sensorManager.unregisterListener(listener, sensor);
    }

    public boolean registerTriggerListener(TriggerEventListener listener) {
        if (sensor.getType() == Sensor.TYPE_SIGNIFICANT_MOTION) {
            return sensorManager.requestTriggerSensor(listener, sensor);
        } else {
            return false;
        }
    }

    public boolean unregisterTriggerListener(TriggerEventListener listener) {
        if (sensor.getType() == Sensor.TYPE_SIGNIFICANT_MOTION) {
            return sensorManager.cancelTriggerSensor(listener, sensor);
        } else {
            return false;
        }
    }

    public String getName() {
        if (hasSensor()) {
            return sensor.getName();
        } else {
            return "Unknown";
        }
    }

    public String getVendor() {
        if (hasSensor()) {
            return sensor.getVendor();
        } else {
            return "Unknown";
        }
    }

    public int getVersion() {
        if (hasSensor()) {
            return sensor.getVersion();
        } else {
            return 0;
        }
    }

    public float getMaximumRange() {
        if (hasSensor()) {
            return sensor.getMaximumRange();
        } else {
            return 0;
        }
    }

    public float getResolution() {
        if (hasSensor()) {
            return sensor.getResolution();
        } else {
            return 0;
        }
    }

    public int getMinDelay() {
        if (hasSensor()) {
            return sensor.getMinDelay();
        } else {
            return 0;
        }
    }

    public float getPower() {
        if (hasSensor()) {
            return sensor.getPower();
        } else {
            return 0;
        }
    }

    public int getType() {
        if (hasSensor()) {
            return sensor.getType();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return description;
    }
}
