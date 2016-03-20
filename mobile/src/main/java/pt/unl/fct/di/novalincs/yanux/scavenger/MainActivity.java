package pt.unl.fct.di.novalincs.yanux.scavenger;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final String TAG = "YANUX_SCAVENGER";

    private final int REQUEST_LOCATION_ACCESS = 0;

    private WifiManager mWifiManager;
    private IntentFilter mWifiScanResultsIntentFilter;
    private BroadcastReceiver mWifiScanResultsBroadcastReceiver;

    private SensorManager mSensorManager;
    private final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;

    private List<Sensor> mSensors;
    private Sensor mAccelerometer;
    private Sensor mAmbientTemperature;
    private Sensor mGravity;
    private Sensor mGyroscope;
    private Sensor mGyroscopeUncalibrated;
    private Sensor mHeartRate;
    private Sensor mLight;
    private Sensor mLinearAcceleration;
    private Sensor mMagneticField;
    private Sensor mMagneticFieldUncalibrated;
    private Sensor mOrientation;
    private Sensor mPressure;
    private Sensor mProximity;
    private Sensor mRelativeHumidity;
    private Sensor mRotationVector;
    private Sensor mGameRotationVector;
    private Sensor mGeomagneticRotationVector;
    private Sensor mSignificantMotion;
    private Sensor mStepCounter;
    private Sensor mStepDetector;
    private Sensor mTemperature;

    private TriggerEventListener mTriggerEventListener = new TriggerEventListener() {
        @Override
        public void onTrigger(TriggerEvent event) {
            Toast.makeText(getApplicationContext(), "Significant motion detected", Toast.LENGTH_SHORT).show();
            TextView textView = (TextView) findViewById(R.id.text_significant_motion);
            textView.setText("Significant Motion\n");
            DecimalFormat formatter = new DecimalFormat("#0.00");
            for(int i = 0; i < event.values.length; i++) {
                textView.setText(textView.getText()+Integer.toString(i)+": "+formatter.format(event.values[i]));
                if(i < event.values.length - 1) {
                    textView.setText(textView.getText()+" ");
                }
            }
            mSensorManager.requestTriggerSensor(this, mSignificantMotion);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        /* Permissions */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageOKCancel("You need to allow access to Location",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_ACCESS);
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_ACCESS);
            }
        }
        /* Wi-Fi */
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiScanning();
        /* Sensors */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        TextView textOutput = (TextView) findViewById(R.id.text_output);
        textOutput.setText("Sensors: ");
        for (Sensor sensor : mSensors) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textOutput.setText(textOutput.getText() +"\n"+ sensor.getStringType()+": ");
            } else {
                textOutput.setText(textOutput.getText() +"\n");
            }*/
            textOutput.setText(textOutput.getText() + "\n" + sensor.getName());
        }
        /* Get a bunch of sensors */
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAmbientTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGyroscopeUncalibrated = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        mHeartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mMagneticFieldUncalibrated = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        /* TODO: Deprecated... Replace if useful. Remove if not! */
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mRelativeHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mGameRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mGeomagneticRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        mSignificantMotion = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SENSOR_DELAY);
        mSensorManager.registerListener(this, mAmbientTemperature, SENSOR_DELAY);
        mSensorManager.registerListener(this, mGravity, SENSOR_DELAY);
        mSensorManager.registerListener(this, mGyroscope, SENSOR_DELAY);
        mSensorManager.registerListener(this, mGyroscopeUncalibrated, SENSOR_DELAY);
        mSensorManager.registerListener(this, mLight, SENSOR_DELAY);
        mSensorManager.registerListener(this, mLinearAcceleration, SENSOR_DELAY);
        mSensorManager.registerListener(this, mMagneticField, SENSOR_DELAY);
        mSensorManager.registerListener(this, mMagneticFieldUncalibrated, SENSOR_DELAY);
        mSensorManager.registerListener(this, mOrientation, SENSOR_DELAY);
        mSensorManager.registerListener(this, mPressure, SENSOR_DELAY);
        mSensorManager.registerListener(this, mProximity, SENSOR_DELAY);
        mSensorManager.registerListener(this, mRelativeHumidity, SENSOR_DELAY);
        mSensorManager.registerListener(this, mRotationVector, SENSOR_DELAY);
        mSensorManager.registerListener(this, mGameRotationVector, SENSOR_DELAY);
        mSensorManager.registerListener(this, mGeomagneticRotationVector, SENSOR_DELAY);
        mSensorManager.registerListener(this, mStepCounter, SENSOR_DELAY);
        mSensorManager.registerListener(this, mStepDetector, SENSOR_DELAY);
        mSensorManager.registerListener(this, mTemperature, SENSOR_DELAY);
        mSensorManager.requestTriggerSensor(mTriggerEventListener, mSignificantMotion);
        wifiScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mWifiScanResultsBroadcastReceiver);
        mSensorManager.cancelTriggerSensor(mTriggerEventListener, mSignificantMotion);
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Access Location Granted!", Toast.LENGTH_SHORT).show();
                    // Permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Access Location Denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        TextView textView = null;
        if(event.sensor == mAccelerometer) {
            textView = (TextView) findViewById(R.id.text_accelerometer);
            textView.setText("Accelerometer\n");
        } else if(event.sensor == mAmbientTemperature) {
            textView = (TextView) findViewById(R.id.text_ambient_temperature);
            textView.setText("Ambient Temperature\n");
        } else if(event.sensor == mGravity) {
            textView = (TextView) findViewById(R.id.text_gravity);
            textView.setText("Gravity\n");
        } else if(event.sensor == mGyroscope) {
            textView = (TextView) findViewById(R.id.text_gyroscope);
            textView.setText("Gyroscope\n");
        } else if(event.sensor == mGyroscopeUncalibrated) {
            textView = (TextView) findViewById(R.id.text_gyroscope_uncalibrated);
            textView.setText("Gyroscope Uncalibrated\n");
        } else if(event.sensor == mHeartRate) {
            textView = (TextView) findViewById(R.id.text_heart_rate);
            textView.setText("Heart Rate\n");
        } else if(event.sensor == mLight) {
            textView = (TextView) findViewById(R.id.text_light);
            textView.setText("Light\n");
        } else if(event.sensor == mLinearAcceleration) {
            textView = (TextView) findViewById(R.id.text_linear_acceleration);
            textView.setText("Linear Acceleration\n");
        } else if(event.sensor == mMagneticField) {
            textView = (TextView) findViewById(R.id.text_magnetic_field);
            textView.setText("Magnetic Field\n");
        } else if(event.sensor == mMagneticFieldUncalibrated) {
            textView = (TextView) findViewById(R.id.text_magnetic_field_uncalibrated);
            textView.setText("Magnetic Field Uncalibrated\n");
        } else if(event.sensor == mOrientation) {
            textView = (TextView) findViewById(R.id.text_orientation);
            textView.setText("Orientation\n");
        } else if(event.sensor == mPressure) {
            textView = (TextView) findViewById(R.id.text_pressure);
            textView.setText("Pressure\n");
        } else if(event.sensor == mProximity) {
            textView = (TextView) findViewById(R.id.text_proximity);
            textView.setText("Proximity\n");
        } else if(event.sensor == mRelativeHumidity) {
            textView = (TextView) findViewById(R.id.text_relative_humidity);
            textView.setText("Relative Humidity\n");
        } else if(event.sensor == mRotationVector) {
            textView = (TextView) findViewById(R.id.text_rotation_vector);
            textView.setText("Rotation Vector\n");
        } else if(event.sensor == mGameRotationVector) {
            textView = (TextView) findViewById(R.id.text_game_rotation_vector);
            textView.setText("Game Rotation Vector\n");
        } else if(event.sensor == mGeomagneticRotationVector) {
            textView = (TextView) findViewById(R.id.text_geomagnetic_rotation_vector);
            textView.setText("Geomagnetic Rotation Vector\n");
        } else if(event.sensor == mStepCounter) {
            textView = (TextView) findViewById(R.id.text_step_counter);
            textView.setText("Step Counter\n");
        } else if(event.sensor == mStepDetector) {
            Toast.makeText(getApplicationContext(), "Step detected", Toast.LENGTH_SHORT).show();
            textView = (TextView) findViewById(R.id.text_step_detector);
            textView.setText("Step Detector\n");
        } else if(event.sensor == mTemperature) {
            textView = (TextView) findViewById(R.id.text_temperature);
            textView.setText("Temperature\n");
        }
        if(textView != null) {
            DecimalFormat formatter = new DecimalFormat("#0.00");
            for(int i = 0; i < event.values.length; i++) {
                textView.setText(textView.getText()+Integer.toString(i)+": "+formatter.format(event.values[i]));
                if(i < event.values.length - 1) {
                    textView.setText(textView.getText()+" ");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void wifiScanning() {
        mWifiManager.setWifiEnabled(true);
        mWifiManager.startScan();
        mWifiScanResultsIntentFilter = new IntentFilter();
        mWifiScanResultsIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mWifiScanResultsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView textView = (TextView) findViewById(R.id.text_wifi_list);
                textView.setText("Results:\n");
                List<ScanResult> results = mWifiManager.getScanResults();
                for (ScanResult result : results) {
                    textView.setText(textView.getText()
                                    + result.SSID
                                    + " (" + result.BSSID + "): "
                                    + result.level
                                    + " [ "+ result.frequency
                                    +" ]\n");
                }
                mWifiManager.startScan();
            }
        };
        registerReceiver(mWifiScanResultsBroadcastReceiver, mWifiScanResultsIntentFilter);
    }
}