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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.R;

public class SensorCollector {
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int NUM_SENSORS = 21;

    private final Context context;
    private final SensorManager sensorManager;

    private final Map<Integer,SensorWrapper> detectedSensors;
    private final Collection<SensorWrapper> sensors;

    private final SensorCollectorListener sensorCollectorListener;
    private SignificantMotionEventListener significantMotionEventListener;

    public SensorCollector(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        detectedSensors = new HashMap<>(NUM_SENSORS);
        detectSensors();

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        sensors = new ArrayList<>(sensorList.size());
        processSensorList(sensorList);

        sensorCollectorListener = new SensorCollectorListener(this);
    }

    private void detectSensors() {
        //Get the whole bunch of sensors
        Sensor accelerometer =  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor ambientTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        Sensor gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor gyroscopeUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        Sensor heartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor magneticFieldUncalibrated =  sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        Sensor pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        Sensor proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor relativeHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        Sensor rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        Sensor gameRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        Sensor geomagneticRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        Sensor significantMotion = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        Sensor stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        //Deprecated Sensors. They are just here for testing purposes
        //TODO: Remove deprecated sensors
        Sensor orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        Sensor temperature = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);

        //Significant Motion Sensor it's the ONLY trigger-based sensor currently available on Android
        significantMotionEventListener = new SignificantMotionEventListener(context, sensorManager, significantMotion);

        if(accelerometer != null) {
            detectedSensors.put(Sensor.TYPE_ACCELEROMETER, new SensorWrapper(context.getString(R.string.sensor_type_accelerometer),accelerometer));
        }
        if(ambientTemperature != null) {
            detectedSensors.put(Sensor.TYPE_AMBIENT_TEMPERATURE, new SensorWrapper(context.getString(R.string.sensor_type_ambient_temperature), ambientTemperature));
        }
        if(gravity != null) {
            detectedSensors.put(Sensor.TYPE_GRAVITY, new SensorWrapper(context.getString(R.string.sensor_type_gravity), gravity));
        }
        if(gyroscope != null) {
            detectedSensors.put(Sensor.TYPE_GYROSCOPE, new SensorWrapper(context.getString(R.string.sensor_type_gyroscope), gyroscope));
        }
        if(gyroscopeUncalibrated != null) {
            detectedSensors.put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, new SensorWrapper(context.getString(R.string.sensor_type_gyroscope_uncalibrated), gyroscopeUncalibrated));
        }
        if(heartRate != null) {
            detectedSensors.put(Sensor.TYPE_HEART_RATE, new SensorWrapper(context.getString(R.string.sensor_type_heart_rate), heartRate));
        }
        if(light != null) {
            detectedSensors.put(Sensor.TYPE_LIGHT, new SensorWrapper(context.getString(R.string.sensor_type_light), light));
        }
        if(linearAcceleration != null){
            detectedSensors.put(Sensor.TYPE_LINEAR_ACCELERATION, new SensorWrapper(context.getString(R.string.sensor_type_linear_acceleration), linearAcceleration));
        }
        if(magneticField != null) {
            detectedSensors.put(Sensor.TYPE_MAGNETIC_FIELD, new SensorWrapper(context.getString(R.string.sensor_type_magnetic_field), magneticField));
        }
        if(magneticFieldUncalibrated != null) {
            detectedSensors.put(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, new SensorWrapper(context.getString(R.string.sensor_type_magnetic_field_uncalibrated), magneticFieldUncalibrated));
        }
        if(pressure != null) {
            detectedSensors.put(Sensor.TYPE_PRESSURE, new SensorWrapper(context.getString(R.string.sensor_type_pressure), pressure));
        }
        if(proximity != null) {
            detectedSensors.put(Sensor.TYPE_PROXIMITY, new SensorWrapper(context.getString(R.string.sensor_type_proximity), proximity));
        }
        if(relativeHumidity != null) {
            detectedSensors.put(Sensor.TYPE_RELATIVE_HUMIDITY, new SensorWrapper(context.getString(R.string.sensor_type_relative_humidity), relativeHumidity));
        }
        if(rotationVector != null) {
            detectedSensors.put(Sensor.TYPE_ROTATION_VECTOR, new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector), rotationVector));
        }
        if(gameRotationVector != null) {
            detectedSensors.put(Sensor.TYPE_GAME_ROTATION_VECTOR, new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector_game), gameRotationVector));
        }
        if(geomagneticRotationVector != null) {
            detectedSensors.put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector_geomagnetic), geomagneticRotationVector));
        }
        if(significantMotion != null) {
            detectedSensors.put(Sensor.TYPE_SIGNIFICANT_MOTION, new SensorWrapper(context.getString(R.string.sensor_type_significant_motion), significantMotion));
        }
        if(stepCounter != null) {
            detectedSensors.put(Sensor.TYPE_STEP_COUNTER, new SensorWrapper(context.getString(R.string.sensor_type_step_counter), stepCounter));
        }
        if(stepDetector != null) {
            detectedSensors.put(Sensor.TYPE_STEP_DETECTOR, new SensorWrapper(context.getString(R.string.sensor_type_step_detector), stepDetector));
        }
        //TODO: Remove deprecated sensors
        if(orientation != null) {
            detectedSensors.put(Sensor.TYPE_ORIENTATION, new SensorWrapper(context.getString(R.string.sensor_type_orientation), orientation));
        }
        if(temperature != null) {
            detectedSensors.put(Sensor.TYPE_TEMPERATURE, new SensorWrapper(context.getString(R.string.sensor_type_temperature), temperature));
        }
    }

    private void processSensorList(List<Sensor> sensorList) {
        for(Sensor sensor : sensorList) {
            switch(sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_accelerometer), sensor));
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_ambient_temperature), sensor));
                    break;
                case Sensor.TYPE_GRAVITY:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_gravity), sensor));
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_gyroscope), sensor));
                    break;
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_gyroscope_uncalibrated), sensor));
                    break;
                case Sensor.TYPE_HEART_RATE:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_heart_rate), sensor));
                    break;
                case Sensor.TYPE_LIGHT:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_light), sensor));
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_linear_acceleration), sensor));
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_magnetic_field), sensor));
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_magnetic_field_uncalibrated), sensor));
                    break;
                case Sensor.TYPE_PRESSURE:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_pressure), sensor));
                    break;
                case Sensor.TYPE_PROXIMITY:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_proximity), sensor));
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_relative_humidity), sensor));
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector), sensor));
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector_game), sensor));
                    break;
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector_geomagnetic), sensor));
                    break;
                case Sensor.TYPE_SIGNIFICANT_MOTION:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_significant_motion), sensor));
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_step_counter), sensor));
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_step_detector), sensor));
                    break;
                //TODO: Remove deprecated sensors
                case Sensor.TYPE_ORIENTATION:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_orientation), sensor));
                    break;
                case Sensor.TYPE_TEMPERATURE:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_temperature), sensor));
                    break;
                default:
                    sensors.add(new SensorWrapper(context.getString(R.string.sensor_type_unknown), sensor));
                    break;
            }
        }
    }

    /*public void registerSensors() {
        sensorManager.registerListener(this.sensorCollectorListener, this.accelerometer.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.ambientTemperature.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.gravity.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.gyroscope.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.gyroscopeUncalibrated.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.light.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.linearAcceleration.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.magneticField.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.magneticFieldUncalibrated.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.pressure.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.proximity.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.relativeHumidity.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.rotationVector.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.gameRotationVector.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.geomagneticRotationVector.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.stepCounter.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.stepDetector.getSensor(), SensorCollector.SENSOR_DELAY);
        //TODO: Remove deprecated sensors
        sensorManager.registerListener(this.sensorCollectorListener, this.orientation.getSensor(), SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.temperature.getSensor(), SensorCollector.SENSOR_DELAY);
        //Significant Motion Sensor it's the ONLY trigger-based sensor currently available on Android
        sensorManager.requestTriggerSensor(this.significantMotionEventListener, this.significantMotion.getSensor());
    }

    public void unregisterSensors() {
        sensorManager.cancelTriggerSensor(this.significantMotionEventListener, this.significantMotion.getSensor());
        sensorManager.unregisterListener(this.sensorCollectorListener);
    }*/

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public Collection<SensorWrapper> getSensors() {
        return detectedSensors.values();
    }

    public Collection<SensorWrapper> getAllSensors() {
        return sensors;
    }
    //Sensor getters

    //Generic Getter
    public SensorWrapper getSensor(int type) {
        return detectedSensors.get(type);
    }

    public SensorWrapper getAccelerometer() {
        return getSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public SensorWrapper getAmbientTemperature() {
        return getSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    public SensorWrapper getGravity() {
        return getSensor(Sensor.TYPE_GRAVITY);
    }

    public SensorWrapper getGyroscope() {
        return getSensor(Sensor.TYPE_GYROSCOPE);
    }

    public SensorWrapper getGyroscopeUncalibrated() {
        return getSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
    }

    public SensorWrapper getHeartRate() {
        return getSensor(Sensor.TYPE_HEART_RATE);
    }

    public SensorWrapper getLight() {
        return getSensor(Sensor.TYPE_LIGHT);
    }

    public SensorWrapper getLinearAcceleration() {
        return getSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public SensorWrapper getMagneticField() {
        return getSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public SensorWrapper getMagneticFieldUncalibrated() {
        return getSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
    }

    public SensorWrapper getOrientation() {
        return getSensor(Sensor.TYPE_ORIENTATION);
    }

    public SensorWrapper getPressure() {
        return getSensor(Sensor.TYPE_PRESSURE);
    }

    public SensorWrapper getProximity() {
        return getSensor(Sensor.TYPE_PROXIMITY);
    }

    public SensorWrapper getRelativeHumidity() {
        return getSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    public SensorWrapper getRotationVector() {
        return getSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public SensorWrapper getGameRotationVector() {
        return getSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
    }

    public SensorWrapper getGeomagneticRotationVector() {
        return getSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
    }

    public SensorWrapper getSignificantMotion() {
        return getSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
    }

    public SensorWrapper getStepCounter() {
        return getSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public SensorWrapper getStepDetector() {
        return getSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public SensorWrapper getTemperature() {
        return getSensor(Sensor.TYPE_TEMPERATURE);
    }

    //Methods to check whether a sensor type is supported or not

    //Generic Checker
    public boolean hasSensor(int type) {
        return detectedSensors.containsKey(type);
    }

    public boolean hasAccelerometer() {
        return hasSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public boolean hasAmbientTemperature() {
        return hasSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    public boolean hasGravity() {
        return hasSensor(Sensor.TYPE_GRAVITY);
    }

    public boolean hasGyroscope() {
        return hasSensor(Sensor.TYPE_GYROSCOPE);
    }

    public boolean hasGyroscopeUncalibrated() {
        return hasSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
    }

    public boolean hasHeartRate() {
        return hasSensor(Sensor.TYPE_HEART_RATE);
    }

    public boolean hasLight() {
        return hasSensor(Sensor.TYPE_LIGHT);
    }

    public boolean hasLinearAcceleration() {
        return hasSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    public boolean hasMagneticField() {
        return hasSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public boolean hasMagneticFieldUncalibrated() {
        return hasSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
    }

    public boolean hasOrientation() {
        return hasSensor(Sensor.TYPE_ORIENTATION);
    }

    public boolean hasPressure() {
        return hasSensor(Sensor.TYPE_PRESSURE);
    }

    public boolean hasProximity() {
        return hasSensor(Sensor.TYPE_PROXIMITY);
    }

    public boolean hasRelativeHumidity() {
        return hasSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    public boolean hasRotationVector() {
        return hasSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public boolean hasGameRotationVector() {
        return hasSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
    }

    public boolean hasGeomagneticRotationVector() {
        return hasSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
    }

    public boolean hasSignificantMotion() {
        return hasSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
    }

    public boolean hasStepCounter() {
        return hasSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public boolean hasStepDetector() {
        return hasSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    public boolean hasTemperature() {
        return hasSensor(Sensor.TYPE_TEMPERATURE);
    }
}