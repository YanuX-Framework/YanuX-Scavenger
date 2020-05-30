/*
 * Copyright (c) 2020 Pedro Albuquerque Santos.
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

import android.hardware.SensorEvent;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorCollector;

public class SensorReading implements IReading {
    private int sensorType;
    private String sensorTypeName;
    private String sensorName;
    private float[] values;
    private int accuracy;
    private long timestamp;

    public SensorReading() {
    }

    public SensorReading(int sensorType, String sensorName, String sensorTypeName, float[] values, int accuracy, long timestamp) {
        this.sensorType = sensorType;
        this.sensorTypeName = sensorTypeName;
        this.sensorName = sensorName;
        this.values = values;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
    }

    public SensorReading(int sensorType, String sensorName, float[] values, int accuracy, long timestamp) {
        this(sensorType, sensorName, SensorCollector.getTypeName(sensorType), values, accuracy, timestamp);
    }

    public SensorReading(SensorEvent event) {
        this(event.sensor.getType(), event.sensor.getName(), event.values, event.accuracy, event.timestamp);
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorTypeName() {
        return sensorTypeName;
    }

    public void setSensorTypeName(String sensorTypeName) {
        this.sensorTypeName = sensorTypeName;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
