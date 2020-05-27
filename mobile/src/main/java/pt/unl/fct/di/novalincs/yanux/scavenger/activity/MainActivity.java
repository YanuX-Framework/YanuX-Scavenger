/*
 * Copyright (c) 2020 Pedro Albuquerque Santos.
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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
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
import pt.unl.fct.di.novalincs.yanux.scavenger.common.capabilities.Capabilities;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.service.MobilePersistentService;
import pt.unl.fct.di.novalincs.yanux.scavenger.service.MobilePersistentService.MobilePersistentServiceBinder;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = Constants.LOG_TAG + "_MAIN_ACTIVITY";
    private PermissionManager permissionManager;
    private Preferences preferences;
    private Capabilities capabilities;
    private MobilePersistentService mobilePersistentService;
    private boolean mobilePersistentServiceBound;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mobilePersistentServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MobilePersistentServiceBinder binder = (MobilePersistentServiceBinder) service;
            mobilePersistentService = binder.getService();
            mobilePersistentServiceBound = true;
            if (!preferences.getYanuxAuthAuthorizationCode().isEmpty()) {
                mobilePersistentService.getPersistentService().exchangeAuthorizationCode();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mobilePersistentServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        permissionManager = new PermissionManager(this);
        /* TODO:
        Develop a proper solution that asks for permission when the application is first
        launched and only then enables the service. In fact, the service should probably check
        for permissions every time it takes an action that needs a certain permission and then
        using somethign similar to this solution
        (https://github.com/mvglasow/satstat/blob/master/src/com/vonglasow/michael/satstat/utils/PermissionHelper.java)
        ask for the permission it needs on-the-fly.
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionManager.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE});
        } else {
            permissionManager.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE});
        }
        preferences = new Preferences(this);

        MobilePersistentService.start(this);

        Uri data = getIntent().getData();
        if (data != null) {
            String authorizationCode = data.getQueryParameter("code");
            Log.d(LOG_TAG, "YanuX Auth Authorization Code: " + authorizationCode);
            preferences.setYanuxAuthAuthorizationCode(authorizationCode);
        }
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
            case R.id.action_kotlin_activity: {
                Intent intent = new Intent(this, KotlinActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MobilePersistentService.class);
        bindService(intent, mobilePersistentServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mobilePersistentServiceConnection);
        mobilePersistentServiceBound = false;
    }
}