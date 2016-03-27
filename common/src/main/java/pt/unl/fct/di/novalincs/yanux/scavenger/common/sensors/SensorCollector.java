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
import java.util.Collections;
import java.util.List;

public class SensorCollector {
    private final Context context;

    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int NUM_SENSORS = 21;

    private final SensorManager sensorManager;

    private final List<Sensor> sensors;

    private final SensorCollectorListener sensorCollectorListener;

    private final Sensor accelerometer;
    private final Sensor ambientTemperature;
    private final Sensor gravity;
    private final Sensor gyroscope;
    private final Sensor gyroscopeUncalibrated;
    private final Sensor heartRate;
    private final Sensor light;
    private final Sensor linearAcceleration;
    private final Sensor magneticField;
    private final Sensor magneticFieldUncalibrated;
    private final Sensor orientation;
    private final Sensor pressure;
    private final Sensor proximity;
    private final Sensor relativeHumidity;
    private final Sensor rotationVector;
    private final Sensor gameRotationVector;
    private final Sensor geomagneticRotationVector;
    private final Sensor significantMotion;
    private final Sensor stepCounter;
    private final Sensor stepDetector;
    private final Sensor temperature;

    private SignificantMotionEventListener significantMotionEventListener;

    public SensorCollector(Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        this.sensors = this.sensorManager.getSensorList(Sensor.TYPE_ALL);
        this.sensorCollectorListener = new SensorCollectorListener(this);

        //Get the whole bunch of sensors
        //TODO: Would it make sense to use a map instead of these many variables?
        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.ambientTemperature = this.sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        this.gravity = this.sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        this.gyroscope = this.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.gyroscopeUncalibrated = this.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        this.heartRate = this.sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        this.light = this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.linearAcceleration = this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.magneticField = this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.magneticFieldUncalibrated = this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        this.pressure = this.sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        this.proximity = this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.relativeHumidity = this.sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        this.rotationVector = this.sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.gameRotationVector = this.sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        this.geomagneticRotationVector = this.sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        this.significantMotion = this.sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        this.stepCounter = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        this.stepDetector = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //Deprecated Sensors. They are just here for testing purposes
        //TODO: Remove deprecated sensors
        this.orientation = this.sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        this.temperature = this.sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);

        //Significant Motion Sensor it's the ONLY trigger-based sensor currently available on Android
        this.significantMotionEventListener     = new SignificantMotionEventListener(context, sensorManager, significantMotion);
    }

    public void registerSensors() {
        sensorManager.registerListener(this.sensorCollectorListener, this.accelerometer, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.ambientTemperature, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.gravity, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.gyroscope, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.gyroscopeUncalibrated, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.light, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.linearAcceleration, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.magneticField, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.magneticFieldUncalibrated, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.pressure, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.proximity, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.relativeHumidity, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.rotationVector, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.gameRotationVector, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.geomagneticRotationVector, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.stepCounter, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.stepDetector, SensorCollector.SENSOR_DELAY);
        //TODO: Remove deprecated sensors
        sensorManager.registerListener(this.sensorCollectorListener, this.orientation, SensorCollector.SENSOR_DELAY);
        sensorManager.registerListener(this.sensorCollectorListener, this.temperature, SensorCollector.SENSOR_DELAY);
        //Significant Motion Sensor it's the ONLY trigger-based sensor currently available on Android
        sensorManager.requestTriggerSensor(this.significantMotionEventListener, this.significantMotion);
    }

    public void unregisterSensors() {
        sensorManager.cancelTriggerSensor(this.significantMotionEventListener, this.significantMotion);
        sensorManager.unregisterListener(this.sensorCollectorListener);
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public List<Sensor> getAllSensors() {
        return sensors;
    }

    public List<Sensor> getDetectedSensors() {
        List<Sensor> detectedSensors = new ArrayList<>(SensorCollector.NUM_SENSORS);
        detectedSensors.add(this.accelerometer);
        detectedSensors.add(this.ambientTemperature);
        detectedSensors.add(this.gravity);
        detectedSensors.add(this.gyroscope);
        detectedSensors.add(this.gyroscopeUncalibrated);
        detectedSensors.add(this.heartRate);
        detectedSensors.add(this.light);
        detectedSensors.add(this.linearAcceleration);
        detectedSensors.add(this.magneticField);
        detectedSensors.add(this.magneticFieldUncalibrated);
        detectedSensors.add(this.pressure);
        detectedSensors.add(this.proximity);
        detectedSensors.add(this.relativeHumidity);
        detectedSensors.add(this.rotationVector);
        detectedSensors.add(this.gameRotationVector);
        detectedSensors.add(this.geomagneticRotationVector);
        detectedSensors.add(this.significantMotion);
        detectedSensors.add(this.stepCounter);
        detectedSensors.add(this.stepDetector);
        //TODO: Remove deprecated sensors
        detectedSensors.add(this.orientation);
        detectedSensors.add(this.temperature);
        //Remove nulls (sensors that are not present)
        detectedSensors.removeAll(Collections.singleton(null));
        return detectedSensors;
    }

    public List<String> getAllSensorNames() {
        return getSensorNames(getAllSensors());
    }

    public List<String> getDetectedSensorNames() {
        return getSensorNames(getDetectedSensors());
    }

    private List<String> getSensorNames(List<Sensor> sensors) {
        List<String> sensorNames = new ArrayList<>(SensorCollector.NUM_SENSORS);
        for(Sensor sensor : sensors) {
            switch(sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    sensorNames.add("Accelerometer");
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    sensorNames.add("Ambient Temperature");
                    break;
                case Sensor.TYPE_GRAVITY:
                    sensorNames.add("Gravity");
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    sensorNames.add("Gyroscope");
                    break;
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    sensorNames.add("Gyroscope Uncalibrated");
                    break;
                case Sensor.TYPE_HEART_RATE:
                    sensorNames.add("Heart Rate");
                    break;
                case Sensor.TYPE_LIGHT:
                    sensorNames.add("Light");
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    sensorNames.add("Type Linear Acceleration");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sensorNames.add("Magnetic Field");
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    sensorNames.add("Magnetic Field Uncalibrated");
                    break;
                case Sensor.TYPE_PRESSURE:
                    sensorNames.add("Pressure");
                    break;
                case Sensor.TYPE_PROXIMITY:
                    sensorNames.add("Proximity");
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    sensorNames.add("Relative Humidity");
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    sensorNames.add("Rotation Vector");
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    sensorNames.add("Rotation Vector Game");
                    break;
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    sensorNames.add("Rotation Vector Geomagnetic");
                    break;
                case Sensor.TYPE_SIGNIFICANT_MOTION:
                    sensorNames.add("Significant Motion");
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    sensorNames.add("Step Counter");
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    sensorNames.add("Step Detector");
                    break;
                //TODO: Remove deprecated sensors
                case Sensor.TYPE_ORIENTATION:
                    sensorNames.add("Orientation");
                    break;
                case Sensor.TYPE_TEMPERATURE:
                    sensorNames.add("Temperature");
                    break;
                default:
                    break;
            }
        }
        return sensorNames;
    }

    //Sensor getters
    public Sensor getAccelerometer() {
        return accelerometer;
    }

    public Sensor getAmbientTemperature() {
        return ambientTemperature;
    }

    public Sensor getGravity() {
        return gravity;
    }

    public Sensor getGyroscope() {
        return gyroscope;
    }

    public Sensor getGyroscopeUncalibrated() {
        return gyroscopeUncalibrated;
    }

    public Sensor getHeartRate() {
        return heartRate;
    }

    public Sensor getLight() {
        return light;
    }

    public Sensor getLinearAcceleration() {
        return linearAcceleration;
    }

    public Sensor getMagneticField() {
        return magneticField;
    }

    public Sensor getMagneticFieldUncalibrated() {
        return magneticFieldUncalibrated;
    }

    public Sensor getOrientation() {
        return orientation;
    }

    public Sensor getPressure() {
        return pressure;
    }

    public Sensor getProximity() {
        return proximity;
    }

    public Sensor getRelativeHumidity() {
        return relativeHumidity;
    }

    public Sensor getRotationVector() {
        return rotationVector;
    }

    public Sensor getGameRotationVector() {
        return gameRotationVector;
    }

    public Sensor getGeomagneticRotationVector() {
        return geomagneticRotationVector;
    }

    public Sensor getSignificantMotion() {
        return significantMotion;
    }

    public Sensor getStepCounter() {
        return stepCounter;
    }

    public Sensor getStepDetector() {
        return stepDetector;
    }

    public Sensor getTemperature() {
        return temperature;
    }

    //Sensor support checkers
    public boolean hasAccelerometer() {
        return accelerometer != null;
    }

    public boolean hasAmbientTemperature() {
        return ambientTemperature != null;
    }

    public boolean hasGravity() {
        return gravity != null;
    }

    public boolean hasGyroscope() {
        return gyroscope != null;
    }

    public boolean hasGyroscopeUncalibrated() {
        return gyroscopeUncalibrated != null;
    }

    public boolean hasHeartRate() {
        return heartRate != null;
    }

    public boolean hasLight() {
        return light != null;
    }

    public boolean hasLinearAcceleration() {
        return linearAcceleration != null;
    }

    public boolean hasMagneticField() {
        return magneticField != null;
    }

    public boolean hasMagneticFieldUncalibrated() {
        return magneticFieldUncalibrated != null;
    }

    public boolean hasOrientation() {
        return orientation != null;
    }

    public boolean hasPressure() {
        return pressure != null;
    }

    public boolean hasProximity() {
        return proximity != null;
    }

    public boolean hasRelativeHumidity() {
        return relativeHumidity != null;
    }

    public boolean hasRotationVector() {
        return rotationVector != null;
    }

    public boolean hasGameRotationVector() {
        return gameRotationVector != null;
    }

    public boolean hasGeomagneticRotationVector() {
        return geomagneticRotationVector != null;
    }

    public boolean hasSignificantMotion() {
        return significantMotion != null;
    }

    public boolean hasStepCounter() {
        return stepCounter != null;
    }

    public boolean hasStepDetector() {
        return stepDetector != null;
    }

    public boolean hasTemperature() {
        return temperature != null;
    }

}
