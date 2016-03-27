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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final int REQUEST_LOCATION_ACCESS = 0;

    private WifiManager wifiManager;
    private IntentFilter wifiScanResultsIntentFilter;
    private BroadcastReceiver wifiScanResultsBroadcastReceiver;

    private SensorManager sensorManager;
    private final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_NORMAL;

    private List<Sensor> sensors;
    private Sensor accelerometer;
    private Sensor ambientTemperature;
    private Sensor gravity;
    private Sensor gyroscope;
    private Sensor gyroscopeUncalibrated;
    private Sensor heartRate;
    private Sensor light;
    private Sensor linearAcceleration;
    private Sensor magneticField;
    private Sensor magneticFieldUncalibrated;
    private Sensor orientation;
    private Sensor pressure;
    private Sensor proximity;
    private Sensor relativeHumidity;
    private Sensor rotationVector;
    private Sensor gameRotationVector;
    private Sensor geomagneticRotationVector;
    private Sensor significantMotion;
    private Sensor stepCounter;
    private Sensor stepDetector;
    private Sensor temperature;

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
            sensorManager.requestTriggerSensor(this, significantMotion);
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
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiScanning();
        /* Sensors */
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        TextView textOutput = (TextView) findViewById(R.id.text_output);
        textOutput.setText("Sensors: ");
        for (Sensor sensor : sensors) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textOutput.setText(textOutput.getText() +"\n"+ sensor.getStringType()+": ");
            } else {
                textOutput.setText(textOutput.getText() +"\n");
            }*/
            textOutput.setText(textOutput.getText() + "\n" + sensor.getName());
        }
        /* Get a bunch of sensors */
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ambientTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroscopeUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        heartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        magneticFieldUncalibrated = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        /* TODO: Deprecated... Replace if useful. Remove if not! */
        orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        relativeHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        gameRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        geomagneticRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        significantMotion = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
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
        switch (id) {
            case R.id.action_sensors: {
                Intent intent = new Intent(this, SensorsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY);
        sensorManager.registerListener(this, ambientTemperature, SENSOR_DELAY);
        sensorManager.registerListener(this, gravity, SENSOR_DELAY);
        sensorManager.registerListener(this, gyroscope, SENSOR_DELAY);
        sensorManager.registerListener(this, gyroscopeUncalibrated, SENSOR_DELAY);
        sensorManager.registerListener(this, light, SENSOR_DELAY);
        sensorManager.registerListener(this, linearAcceleration, SENSOR_DELAY);
        sensorManager.registerListener(this, magneticField, SENSOR_DELAY);
        sensorManager.registerListener(this, magneticFieldUncalibrated, SENSOR_DELAY);
        sensorManager.registerListener(this, orientation, SENSOR_DELAY);
        sensorManager.registerListener(this, pressure, SENSOR_DELAY);
        sensorManager.registerListener(this, proximity, SENSOR_DELAY);
        sensorManager.registerListener(this, relativeHumidity, SENSOR_DELAY);
        sensorManager.registerListener(this, rotationVector, SENSOR_DELAY);
        sensorManager.registerListener(this, gameRotationVector, SENSOR_DELAY);
        sensorManager.registerListener(this, geomagneticRotationVector, SENSOR_DELAY);
        sensorManager.registerListener(this, stepCounter, SENSOR_DELAY);
        sensorManager.registerListener(this, stepDetector, SENSOR_DELAY);
        sensorManager.registerListener(this, temperature, SENSOR_DELAY);
        sensorManager.requestTriggerSensor(mTriggerEventListener, significantMotion);
        wifiScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiScanResultsBroadcastReceiver);
        sensorManager.cancelTriggerSensor(mTriggerEventListener, significantMotion);
        sensorManager.unregisterListener(this);
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
        if(event.sensor == accelerometer) {
            textView = (TextView) findViewById(R.id.text_accelerometer);
            textView.setText("Accelerometer\n");
        } else if(event.sensor == ambientTemperature) {
            textView = (TextView) findViewById(R.id.text_ambient_temperature);
            textView.setText("Ambient Temperature\n");
        } else if(event.sensor == gravity) {
            textView = (TextView) findViewById(R.id.text_gravity);
            textView.setText("Gravity\n");
        } else if(event.sensor == gyroscope) {
            textView = (TextView) findViewById(R.id.text_gyroscope);
            textView.setText("Gyroscope\n");
        } else if(event.sensor == gyroscopeUncalibrated) {
            textView = (TextView) findViewById(R.id.text_gyroscope_uncalibrated);
            textView.setText("Gyroscope Uncalibrated\n");
        } else if(event.sensor == heartRate) {
            textView = (TextView) findViewById(R.id.text_heart_rate);
            textView.setText("Heart Rate\n");
        } else if(event.sensor == light) {
            textView = (TextView) findViewById(R.id.text_light);
            textView.setText("Light\n");
        } else if(event.sensor == linearAcceleration) {
            textView = (TextView) findViewById(R.id.text_linear_acceleration);
            textView.setText("Linear Acceleration\n");
        } else if(event.sensor == magneticField) {
            textView = (TextView) findViewById(R.id.text_magnetic_field);
            textView.setText("Magnetic Field\n");
        } else if(event.sensor == magneticFieldUncalibrated) {
            textView = (TextView) findViewById(R.id.text_magnetic_field_uncalibrated);
            textView.setText("Magnetic Field Uncalibrated\n");
        } else if(event.sensor == orientation) {
            textView = (TextView) findViewById(R.id.text_orientation);
            textView.setText("Orientation\n");
        } else if(event.sensor == pressure) {
            textView = (TextView) findViewById(R.id.text_pressure);
            textView.setText("Pressure\n");
        } else if(event.sensor == proximity) {
            textView = (TextView) findViewById(R.id.text_proximity);
            textView.setText("Proximity\n");
        } else if(event.sensor == relativeHumidity) {
            textView = (TextView) findViewById(R.id.text_relative_humidity);
            textView.setText("Relative Humidity\n");
        } else if(event.sensor == rotationVector) {
            textView = (TextView) findViewById(R.id.text_rotation_vector);
            textView.setText("Rotation Vector\n");
        } else if(event.sensor == gameRotationVector) {
            textView = (TextView) findViewById(R.id.text_game_rotation_vector);
            textView.setText("Game Rotation Vector\n");
        } else if(event.sensor == geomagneticRotationVector) {
            textView = (TextView) findViewById(R.id.text_geomagnetic_rotation_vector);
            textView.setText("Geomagnetic Rotation Vector\n");
        } else if(event.sensor == stepCounter) {
            textView = (TextView) findViewById(R.id.text_step_counter);
            textView.setText("Step Counter\n");
        } else if(event.sensor == stepDetector) {
            Toast.makeText(getApplicationContext(), "Step detected", Toast.LENGTH_SHORT).show();
            textView = (TextView) findViewById(R.id.text_step_detector);
            textView.setText("Step Detector\n");
        } else if(event.sensor == temperature) {
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
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        wifiScanResultsIntentFilter = new IntentFilter();
        wifiScanResultsIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        wifiScanResultsBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView textView = (TextView) findViewById(R.id.text_wifi_list);
                textView.setText("Results:\n");
                List<ScanResult> results = wifiManager.getScanResults();
                for (ScanResult result : results) {
                    textView.setText(textView.getText()
                                    + result.SSID
                                    + " (" + result.BSSID + "): "
                                    + result.level
                                    + " [ "+ result.frequency
                                    +" ]\n");
                }
                wifiManager.startScan();
            }
        };
        registerReceiver(wifiScanResultsBroadcastReceiver, wifiScanResultsIntentFilter);
    }
}