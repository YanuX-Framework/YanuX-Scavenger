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

public class SensorReadings implements SensorEventListener {
    private static final String LOG_TAG = Constants.LOG_TAG + "_SENSOR_READINGS";
    Map<Sensor, SensorLoggable> sensorReadings;

    public SensorReadings() {
        sensorReadings = new HashMap<>();
    }

    public List<SensorLoggable> getCurrentReadings() {
        List<SensorLoggable> currentReadings = new ArrayList<>(sensorReadings.size());
        for (SensorLoggable sensorSample : sensorReadings.values()) {
            currentReadings.add(sensorSample);
        }
        return currentReadings;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(LOG_TAG, "Sensor Changed: " + event.sensor.getName());
        sensorReadings.put(event.sensor, new SensorLoggable(event));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(LOG_TAG, "Sensor: " + sensor.getName() + " Accuracy Changed: " + accuracy);
    }
}
