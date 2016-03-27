package pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.widget.Toast;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.R;

/**
 * Created by Pedro on 20/03/2016.
 */
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
            Toast.makeText(SignificantMotionEventListener.this.context, R.string.significant_motion_detected, Toast.LENGTH_SHORT).show();
            sensorManager.requestTriggerSensor(this, sensor);
        }
    }
}
