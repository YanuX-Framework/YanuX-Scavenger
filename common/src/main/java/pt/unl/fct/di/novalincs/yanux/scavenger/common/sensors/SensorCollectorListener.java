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
        for(int i = 0; i < event.values.length; i++) {
            output += i+": "+formatter.format(event.values[i]);
            if(i < event.values.length - 1) {
                output+=" ";
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
