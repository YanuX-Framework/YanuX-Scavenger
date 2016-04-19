package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class SensorLog implements SensorEventListener {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + SensorLog.class.getSimpleName();
    //I'm keeping everything in a Dequeue just for fun! But I might as well just keep the last value for each sensor to save memory
    Map<Sensor, Deque<SensorLogEntry>> sensorReadings;

    public SensorLog() {
        sensorReadings = new HashMap<>();
    }

    public List<SensorLogEntry> getCurrentReadings() {
        List<SensorLogEntry> currentReadings = new ArrayList<>(sensorReadings.size());
        for (Deque<SensorLogEntry> sensorLogEntry : sensorReadings.values()) {
            currentReadings.add(sensorLogEntry.peekFirst());
        }
        return currentReadings;
    }

    public void clear() {
        sensorReadings.clear();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(LOG_TAG, "Sensor Changed: " + event.sensor.getName());
        if (!sensorReadings.containsKey(event.sensor)) {
            sensorReadings.put(event.sensor, new ArrayDeque<SensorLogEntry>());
        }
        Deque<SensorLogEntry> reading = sensorReadings.get(event.sensor);
        reading.addFirst(new SensorLogEntry(event));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(LOG_TAG, "Sensor: " + sensor.getName() + " Accuracy Changed: " + accuracy);
    }
}
