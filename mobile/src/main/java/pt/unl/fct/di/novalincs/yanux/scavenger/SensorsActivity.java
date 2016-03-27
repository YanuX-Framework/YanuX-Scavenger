package pt.unl.fct.di.novalincs.yanux.scavenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.LinkedList;
import java.util.List;

public class SensorsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        Spinner spinnerSensors = (Spinner) findViewById(R.id.spinner_sensors);

        List<String> sensorList = new LinkedList<>();
        sensorList.add("Accelerometer");
        sensorList.add("Gyroscope");
        sensorList.add("Magnetometer");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sensorList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSensors.setAdapter(adapter);
    }
}
