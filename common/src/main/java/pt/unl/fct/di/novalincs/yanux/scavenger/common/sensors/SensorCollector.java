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

import pt.unl.fct.di.novalincs.yanux.scavenger.common.R;

public class SensorCollector {
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;
    private static final int NUM_SENSORS = 21;
    private final Context context;
    private final SensorManager sensorManager;

    private final List<Sensor> sensors;

    private final SensorCollectorListener sensorCollectorListener;

    private final SensorWrapper accelerometer;
    private final SensorWrapper ambientTemperature;
    private final SensorWrapper gravity;
    private final SensorWrapper gyroscope;
    private final SensorWrapper gyroscopeUncalibrated;
    private final SensorWrapper heartRate;
    private final SensorWrapper light;
    private final SensorWrapper linearAcceleration;
    private final SensorWrapper magneticField;
    private final SensorWrapper magneticFieldUncalibrated;
    private final SensorWrapper orientation;
    private final SensorWrapper pressure;
    private final SensorWrapper proximity;
    private final SensorWrapper relativeHumidity;
    private final SensorWrapper rotationVector;
    private final SensorWrapper gameRotationVector;
    private final SensorWrapper geomagneticRotationVector;
    private final SensorWrapper significantMotion;
    private final SensorWrapper stepCounter;
    private final SensorWrapper stepDetector;
    private final SensorWrapper temperature;

    private SignificantMotionEventListener significantMotionEventListener;

    public SensorCollector(Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        this.sensors = this.sensorManager.getSensorList(Sensor.TYPE_ALL);
        this.sensorCollectorListener = new SensorCollectorListener(this);

        //Get the whole bunch of sensors
        //TODO: Would it make sense to use a map instead of these many variables?
        this.accelerometer = new SensorWrapper(context.getString(R.string.sensor_type_accelerometer),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));

        this.ambientTemperature = new SensorWrapper(context.getString(R.string.sensor_type_ambient_temperature),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE));

        this.gravity = new SensorWrapper(context.getString(R.string.sensor_type_gravity),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));

        this.gyroscope = new SensorWrapper(context.getString(R.string.sensor_type_gyroscope),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));

        this.gyroscopeUncalibrated = new SensorWrapper(context.getString(R.string.sensor_type_gyroscope_uncalibrated),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED));

        this.heartRate = new SensorWrapper(context.getString(R.string.sensor_type_heart_rate),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE));

        this.light = new SensorWrapper(context.getString(R.string.sensor_type_light),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));

        this.linearAcceleration = new SensorWrapper(context.getString(R.string.sensor_type_linear_acceleration),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));

        this.magneticField = new SensorWrapper(context.getString(R.string.sensor_type_magnetic_field),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));

        this.magneticFieldUncalibrated = new SensorWrapper(context.getString(R.string.sensor_type_magnetic_field_uncalibrated),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED));

        this.pressure = new SensorWrapper(context.getString(R.string.sensor_type_pressure),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));

        this.proximity = new SensorWrapper(context.getString(R.string.sensor_type_proximity),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));

        this.relativeHumidity = new SensorWrapper(context.getString(R.string.sensor_type_relative_humidity),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY));

        this.rotationVector = new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));

        this.gameRotationVector = new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector_game),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR));

        this.geomagneticRotationVector = new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector_geomagnetic),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR));

        this.significantMotion = new SensorWrapper(context.getString(R.string.sensor_type_significant_motion),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION));

        this.stepCounter = new SensorWrapper(context.getString(R.string.sensor_type_step_counter),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER));

        this.stepDetector = new SensorWrapper(context.getString(R.string.sensor_type_step_detector),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR));
        //Deprecated Sensors. They are just here for testing purposes
        //TODO: Remove deprecated sensors
        this.orientation = new SensorWrapper(context.getString(R.string.sensor_type_orientation),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));

        this.temperature = new SensorWrapper(context.getString(R.string.sensor_type_temperature),
                this.sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE));

        //Significant Motion Sensor it's the ONLY trigger-based sensor currently available on Android
        this.significantMotionEventListener = new SignificantMotionEventListener(context, sensorManager, significantMotion.getSensor());
    }

    public void registerSensors() {
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
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public List<SensorWrapper> getSensors() {
        List<SensorWrapper> detectedSensors = new ArrayList<>(SensorCollector.NUM_SENSORS);
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
        //TODO: Fix this! This is not removing sensors which are not present!
        //Remove nulls (sensors that are not present)
        detectedSensors.removeAll(Collections.singleton(null));
        return detectedSensors;
    }

    public List<SensorWrapper> getAllSensors() {
        List<SensorWrapper> sensorsList = new ArrayList<>(sensors.size());
        for(Sensor sensor : sensors) {
            switch(sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_accelerometer), sensor));
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_ambient_temperature), sensor));
                    break;
                case Sensor.TYPE_GRAVITY:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_gravity), sensor));
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_gyroscope), sensor));
                    break;
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_gyroscope_uncalibrated), sensor));
                    break;
                case Sensor.TYPE_HEART_RATE:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_heart_rate), sensor));
                    break;
                case Sensor.TYPE_LIGHT:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_light), sensor));
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_linear_acceleration), sensor));
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_magnetic_field), sensor));
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_magnetic_field_uncalibrated), sensor));
                    break;
                case Sensor.TYPE_PRESSURE:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_pressure), sensor));
                    break;
                case Sensor.TYPE_PROXIMITY:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_proximity), sensor));
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_relative_humidity), sensor));
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector), sensor));
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector_game), sensor));
                    break;
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_rotation_vector_geomagnetic), sensor));
                    break;
                case Sensor.TYPE_SIGNIFICANT_MOTION:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_significant_motion), sensor));
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_step_counter), sensor));
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_step_detector), sensor));
                    break;
                //TODO: Remove deprecated sensors
                case Sensor.TYPE_ORIENTATION:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_orientation), sensor));
                    break;
                case Sensor.TYPE_TEMPERATURE:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_temperature), sensor));
                    break;
                default:
                    sensorsList.add(new SensorWrapper(context.getString(R.string.sensor_type_unknown), sensor));
                    break;
            }
        }
        return sensorsList;
    }

    //Sensor getters
    public Sensor getAccelerometer() {
        return accelerometer.getSensor();
    }

    public Sensor getAmbientTemperature() {
        return ambientTemperature.getSensor();
    }

    public Sensor getGravity() {
        return gravity.getSensor();
    }

    public Sensor getGyroscope() {
        return gyroscope.getSensor();
    }

    public Sensor getGyroscopeUncalibrated() {
        return gyroscopeUncalibrated.getSensor();
    }

    public Sensor getHeartRate() {
        return heartRate.getSensor();
    }

    public Sensor getLight() {
        return light.getSensor();
    }

    public Sensor getLinearAcceleration() {
        return linearAcceleration.getSensor();
    }

    public Sensor getMagneticField() {
        return magneticField.getSensor();
    }

    public Sensor getMagneticFieldUncalibrated() {
        return magneticFieldUncalibrated.getSensor();
    }

    public Sensor getOrientation() {
        return orientation.getSensor();
    }

    public Sensor getPressure() {
        return pressure.getSensor();
    }

    public Sensor getProximity() {
        return proximity.getSensor();
    }

    public Sensor getRelativeHumidity() {
        return relativeHumidity.getSensor();
    }

    public Sensor getRotationVector() {
        return rotationVector.getSensor();
    }

    public Sensor getGameRotationVector() {
        return gameRotationVector.getSensor();
    }

    public Sensor getGeomagneticRotationVector() {
        return geomagneticRotationVector.getSensor();
    }

    public Sensor getSignificantMotion() {
        return significantMotion.getSensor();
    }

    public Sensor getStepCounter() {
        return stepCounter.getSensor();
    }

    public Sensor getStepDetector() {
        return stepDetector.getSensor();
    }

    public Sensor getTemperature() {
        return temperature.getSensor();
    }

    //Sensor support checkers
    public boolean hasAccelerometer() {
        return accelerometer.hasSensor();
    }

    public boolean hasAmbientTemperature() {
        return ambientTemperature.hasSensor();
    }

    public boolean hasGravity() {
        return gravity.hasSensor();
    }

    public boolean hasGyroscope() {
        return gyroscope.hasSensor();
    }

    public boolean hasGyroscopeUncalibrated() {
        return gyroscopeUncalibrated.hasSensor();
    }

    public boolean hasHeartRate() {
        return heartRate.hasSensor();
    }

    public boolean hasLight() {
        return light.hasSensor();
    }

    public boolean hasLinearAcceleration() {
        return linearAcceleration.hasSensor();
    }

    public boolean hasMagneticField() {
        return magneticField.hasSensor();
    }

    public boolean hasMagneticFieldUncalibrated() {
        return magneticFieldUncalibrated.hasSensor();
    }

    public boolean hasOrientation() {
        return orientation.hasSensor();
    }

    public boolean hasPressure() {
        return pressure.hasSensor();
    }

    public boolean hasProximity() {
        return proximity.hasSensor();
    }

    public boolean hasRelativeHumidity() {
        return relativeHumidity.hasSensor();
    }

    public boolean hasRotationVector() {
        return rotationVector.hasSensor();
    }

    public boolean hasGameRotationVector() {
        return gameRotationVector.hasSensor();
    }

    public boolean hasGeomagneticRotationVector() {
        return geomagneticRotationVector.hasSensor();
    }

    public boolean hasSignificantMotion() {
        return significantMotion.hasSensor();
    }

    public boolean hasStepCounter() {
        return stepCounter.hasSensor();
    }

    public boolean hasStepDetector() {
        return stepDetector.hasSensor();
    }

    public boolean hasTemperature() {
        return temperature.hasSensor();
    }

}