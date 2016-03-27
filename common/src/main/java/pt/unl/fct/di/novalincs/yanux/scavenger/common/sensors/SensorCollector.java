package pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by Pedro on 20/03/2016.
 */
public class SensorCollector {
    private final Context context;

    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;

    private final SensorManager mSensorManager;

    private final List<Sensor> mSensors;

    private final SensorCollectorListener mSensorCollectorListener;

    private final Sensor mAccelerometer;
    private final Sensor mAmbientTemperature;
    private final Sensor mGravity;
    private final Sensor mGyroscope;
    private final Sensor mGyroscopeUncalibrated;
    private final Sensor mHeartRate;
    private final Sensor mLight;
    private final Sensor mLinearAcceleration;
    private final Sensor mMagneticField;
    private final Sensor mMagneticFieldUncalibrated;
    private final Sensor mOrientation;
    private final Sensor mPressure;
    private final Sensor mProximity;
    private final Sensor mRelativeHumidity;
    private final Sensor mRotationVector;
    private final Sensor mGameRotationVector;
    private final Sensor mGeomagneticRotationVector;
    private final Sensor mSignificantMotion;
    private final Sensor mStepCounter;
    private final Sensor mStepDetector;
    private final Sensor mTemperature;

    private SignificantMotionEventListener significantMotionEventListener;

    public SensorCollector(Context context) {
        this.context = context;
        this.mSensorManager = (SensorManager) this.context.getSystemService(Context.SENSOR_SERVICE);
        this.mSensors = this.mSensorManager.getSensorList(Sensor.TYPE_ALL);
        this.mSensorCollectorListener = new SensorCollectorListener(this);

        /* Get the whole bunch of sensors */
        this.mAccelerometer                     = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.mAmbientTemperature                = this.mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        this.mGravity                           = this.mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        this.mGyroscope                         = this.mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.mGyroscopeUncalibrated             = this.mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        this.mHeartRate                         = this.mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        this.mLight                             = this.mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.mLinearAcceleration                = this.mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.mMagneticField                     = this.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.mMagneticFieldUncalibrated         = this.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        this.mPressure                          = this.mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        this.mProximity                         = this.mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.mRelativeHumidity                  = this.mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        this.mRotationVector                    = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.mGameRotationVector                = this.mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        this.mGeomagneticRotationVector         = this.mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        this.mSignificantMotion                 = this.mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        this.mStepCounter                       = this.mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        this.mStepDetector                      = this.mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //Deprecated Sensors. They are just here for testing purposes
        //TODO: Remove deprecated sensors
        this.mOrientation                       = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        this.mTemperature                       = this.mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);

        //Significant Motion Sensor it's the ONLY trigger-based sensor currently available on Android
        this.significantMotionEventListener     = new SignificantMotionEventListener(context, mSensorManager, mSignificantMotion);
    }

    public void registerSensors() {
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mAccelerometer,             SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mAmbientTemperature,        SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mGravity,                   SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mGyroscope,                 SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mGyroscopeUncalibrated,     SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mLight,                     SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mLinearAcceleration,        SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mMagneticField,             SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mMagneticFieldUncalibrated, SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mPressure,                  SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mProximity,                 SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mRelativeHumidity,          SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mRotationVector,            SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mGameRotationVector,        SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mGeomagneticRotationVector, SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mStepCounter,               SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mStepDetector,              SensorCollector.SENSOR_DELAY);
        //TODO: Remove deprecated sensors
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mOrientation,               SensorCollector.SENSOR_DELAY);
        mSensorManager.registerListener(this.mSensorCollectorListener, this.mTemperature,               SensorCollector.SENSOR_DELAY);
        //Significant Motion Sensor it's the ONLY trigger-based sensor currently available on Android
        mSensorManager.requestTriggerSensor(this.significantMotionEventListener, this.mSignificantMotion);
    }
}
