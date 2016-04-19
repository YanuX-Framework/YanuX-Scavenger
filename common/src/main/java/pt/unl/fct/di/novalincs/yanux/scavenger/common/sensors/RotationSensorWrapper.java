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
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;

public class RotationSensorWrapper extends SensorWrapper implements SensorEventListener {
    private Sensor accelerometer;
    private Sensor magneticField;

    private boolean lastAccelerometerSet = false;
    private boolean lastMagneticFieldSet = false;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagneticField = new float[3];

    private float[] inclinationMatrix = new float[9];
    private float[] rotationMatrix = new float[9];
    private float inclination;
    private float[] orientation = new float[3];

    public RotationSensorWrapper(SensorManager sensorManager, String description, Sensor accelerometer, Sensor magneticField) {
        super(sensorManager, description, null);
        this.accelerometer = accelerometer;
        this.magneticField = magneticField;
    }

    @Override
    public boolean registerListener(SensorEventListener listener) {
        return registerListener(listener, SensorCollector.SENSOR_DELAY);
    }

    @Override
    public boolean registerListener(SensorEventListener listener, int delay) {
        return getSensorManager().registerListener(this, accelerometer, delay)
                && getSensorManager().registerListener(this, magneticField, delay)
                && getSensorManager().registerListener(listener, accelerometer, delay)
                && getSensorManager().registerListener(listener, magneticField, delay);
    }

    @Override
    public void unregisterListener(SensorEventListener listener) {
        getSensorManager().unregisterListener(this, accelerometer);
        getSensorManager().unregisterListener(this, magneticField);
        getSensorManager().unregisterListener(listener, accelerometer);
        getSensorManager().unregisterListener(listener, magneticField);
    }

    @Override
    public boolean registerTriggerListener(TriggerEventListener listener) {
        return false;
    }

    @Override
    public boolean unregisterTriggerListener(TriggerEventListener listener) {
        return false;
    }

    @Override
    public String getName() {
        return "Rotation Sensor Wrapper";
    }

    @Override
    public String getVendor() {
        return "Yanux, Inc.";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public float getMaximumRange() {
        return super.getMaximumRange();
    }

    @Override
    public float getResolution() {
        return super.getResolution();
    }

    @Override
    public int getMinDelay() {
        return Math.max(accelerometer.getMinDelay(), magneticField.getMinDelay());
    }

    @Override
    public float getPower() {
        return accelerometer.getPower() + magneticField.getPower();
    }

    @Override
    public int getType() {
        return SensorCollector.ROTATION_SENSOR_WRAPPER;
    }

    public float[] getOrientation() {
        return orientation;
    }

    public float[] getRotationMatrix() {
        return rotationMatrix;
    }

    public float[] getInclinationMatrix() {
        return inclinationMatrix;
    }

    public float getInclination() {
        return inclination;
    }

    /**
     * TODO: Another possibility, rather than relying on the client to check the derived values, would be to wrap everything under this class and only notify the real client when sensor data is available and/or changes.
     * The same idea could even be used for the regular sensors in order to enrich the data passed onto them, since the default SensorEvent class sometimes doesn't provide all I want/need.
     */
    /**
     * Called when sensor values have changed.
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p/>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magneticField) {
            System.arraycopy(event.values, 0, lastMagneticField, 0, event.values.length);
            lastMagneticFieldSet = true;
        }
        if (lastAccelerometerSet && lastMagneticFieldSet) {
            SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, lastAccelerometer, lastMagneticField);
            SensorManager.getOrientation(rotationMatrix, orientation);
            inclination = SensorManager.getInclination(inclinationMatrix);
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
