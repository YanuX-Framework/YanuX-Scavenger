/*
 * Copyright (c) 2018 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.audio.AudioActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.beacons.BeaconsActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.bluetooth.BluetoothClassicActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.bluetooth.BluetoothLeActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.nearby.NearbyConnectionsActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.nearby.NearbyMessagesActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.preferences.PreferencesActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.sensors.SensorsActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.wifi.WifiActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.service.MobileService;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = Constants.LOG_TAG + "_MAIN_ACTIVITY";
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if (data != null) {
            /*
             * TODO: Do something with the extracted code!
             */
            Log.d(LOG_TAG, "Code: " + data.getQueryParameter("code"));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        permissionManager = new PermissionManager(this);
        permissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        MobileService.start(this);
        /*
         * Ot alternatively I can convert it to a JobService managed by JobScheduler
         */
//        ComponentName componentName = new ComponentName(this, MobileService.class);
//        JobInfo jobInfo = new JobInfo.Builder(12, componentName)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .build();
//        JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
//        int resultCode = jobScheduler.schedule(jobInfo);
//        if (resultCode == JobScheduler.RESULT_SUCCESS) {
//            Log.d(LOG_TAG, "Job scheduled!");
//        } else {
//            Log.d(LOG_TAG, "Job not scheduled");
//        }
        /*
         * NOTE: The following text is just used to test if the scrolling behavior is working properly in the app's layout!
         */
//        TextView textOuput = (TextView) findViewById(R.id.text_output);
//        textOuput.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin et hendrerit nulla. Sed commodo imperdiet semper. Fusce vitae lacus eget nisi fringilla pharetra efficitur nec turpis. Cras pharetra eleifend erat, quis vestibulum nulla facilisis sed. Sed nec ex urna. Proin feugiat efficitur dui, sit amet viverra neque rutrum id. Pellentesque luctus molestie lectus vel aliquam. Nullam malesuada dolor vel lorem auctor, ac pulvinar tellus eleifend. Nulla fermentum dignissim consectetur. Quisque dignissim nisi volutpat, imperdiet metus ut, condimentum velit. Suspendisse potenti. Aliquam porttitor lobortis dui, vel egestas mauris vestibulum vitae.\n" +
//                "\n" +
//                "Duis eget lectus vitae nisi auctor venenatis. In ultricies, massa ac facilisis tempus, massa velit ultrices sem, eget lacinia velit eros quis dolor. Nulla facilisi. Suspendisse gravida ipsum risus, at vestibulum sem congue lacinia. Aenean dignissim magna felis, id dictum ex lacinia ut. Vivamus massa ligula, condimentum a eleifend in, interdum sit amet magna. Duis egestas velit sed dapibus feugiat. Vivamus ut nisl sapien. Duis facilisis laoreet consectetur. Nullam sollicitudin viverra neque, eu lacinia augue cursus at. Phasellus facilisis dolor in nulla vulputate, eleifend interdum ligula tempor. Proin blandit tincidunt malesuada.\n" +
//                "\n" +
//                "Curabitur nibh arcu, laoreet sit amet vehicula et, hendrerit ut nisi. Curabitur risus lacus, ullamcorper at efficitur eu, fringilla in dui. Phasellus sit amet elementum magna. Pellentesque nec nisi justo. Cras porttitor at felis vitae pellentesque. Cras porttitor ligula elit, in viverra sapien posuere sed. Praesent euismod metus lorem, vel tristique nulla hendrerit a. Nullam sed eleifend ex. Sed in nulla porttitor, volutpat mi et, tincidunt augue. Aenean elementum dui in dui tempor, quis semper ligula interdum.\n" +
//                "\n" +
//                "Nam suscipit diam tellus, et luctus nisl lobortis eu. Nunc non tellus sem. Cras congue sodales arcu ultricies maximus. Praesent luctus ut felis in laoreet. Nunc ornare rutrum feugiat. Integer fringilla sagittis posuere. Donec euismod scelerisque sapien.\n" +
//                "\n" +
//                "Integer consequat eros risus. Etiam id malesuada velit. Ut non libero iaculis, sagittis elit at, faucibus ipsum. Nam in nibh ullamcorper, gravida urna non, faucibus ex. Suspendisse pretium nec nunc at sollicitudin. Duis tincidunt magna ut efficitur mollis. Cras finibus mauris mollis, tempus lorem quis, luctus mauris. Nam consectetur imperdiet diam, non porta urna commodo vitae.");
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
            case R.id.action_wifi: {
                Intent intent = new Intent(this, WifiActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_bluetooth_classic: {
                Intent intent = new Intent(this, BluetoothClassicActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_bluetooth_le: {
                Intent intent = new Intent(this, BluetoothLeActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_beacons: {
                Intent intent = new Intent(this, BeaconsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_audio: {
                Intent intent = new Intent(this, AudioActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_nearby_connections: {
                Intent intent = new Intent(this, NearbyConnectionsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_nearby_messages: {
                Intent intent = new Intent(this, NearbyMessagesActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_preferences: {
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}