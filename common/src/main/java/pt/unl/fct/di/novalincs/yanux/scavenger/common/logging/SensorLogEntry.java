package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

import android.hardware.SensorEvent;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorCollector;

public class SensorLogEntry {
    private int sensorType;
    private String sensorTypeName;
    private String sensorName;
    private float[] values;
    private int accuracy;
    private long timestamp;

    public SensorLogEntry(int sensorType, String sensorName, String sensorTypeName, float[] values, int accuracy, long timestamp) {
        this.sensorType = sensorType;
        this.sensorTypeName = sensorTypeName;
        this.sensorName = sensorName;
        this.values = values;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
    }

    public SensorLogEntry(int sensorType, String sensorName, float[] values, int accuracy, long timestamp) {
        this(sensorType, sensorName, SensorCollector.getTypeName(sensorType), values, accuracy, timestamp);
    }

    public SensorLogEntry(SensorEvent event) {
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
