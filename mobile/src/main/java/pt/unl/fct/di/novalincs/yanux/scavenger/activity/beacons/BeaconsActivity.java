/*
 * Copyright (c) 2019 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.beacons;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.altbeacon.beacon.BeaconConsumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconWrapper;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.IFileLogger;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.JsonFileLogger;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Utilities;
import pt.unl.fct.di.novalincs.yanux.scavenger.view.RecyclerViewSimpleListAdapter;

public class BeaconsActivity extends AppCompatActivity implements BeaconConsumer {

    public static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + BeaconsActivity.class.getSimpleName();
    private PermissionManager permissionManager;
    private IFileLogger logger;
    private long loggingStartTime;
    private ToggleButton beaconLogToggleButton;
    private EditText beaconLogFilenameEditText;
    private EditText beaconLogTimeEditText;
    private BeaconCollector beaconCollector;
    private boolean beaconServiceConnected = false;
    private RecyclerView beaconList;
    private RecyclerViewSimpleListAdapter<BeaconWrapper> beaconListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_beacons);
        permissionManager = new PermissionManager(this);
        permissionManager.requestPermissions(REQUIRED_PERMISSIONS);

        logger = new JsonFileLogger(this);
        beaconLogToggleButton = findViewById(R.id.beacons_log_toggle_button);
        beaconLogFilenameEditText = findViewById(R.id.beacons_log_filename_edit_text);
        beaconLogToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startLogging(beaconLogFilenameEditText.getText().toString());
                } else {
                    stopLogging();
                }
            }
        });
        beaconLogTimeEditText = findViewById(R.id.beacons_log_time_edit_text);

        beaconList = findViewById(R.id.beacons_list_recycler_view);
        beaconList.setLayoutManager(new LinearLayoutManager(this));
        beaconListAdapter = new RecyclerViewSimpleListAdapter<>(new ArrayList<BeaconWrapper>());
        beaconList.setAdapter(beaconListAdapter);
        beaconCollector = new BeaconCollector(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BeaconCollector.ACTION_BEACON_RANGE_BEACONS:
                        List<BeaconWrapper> beaconsArrayList = intent.getParcelableArrayListExtra(BeaconCollector.EXTRA_BEACONS);
                        beaconListAdapter.setDataSet(beaconsArrayList);
                        beaconListAdapter.notifyDataSetChanged();
                        TextView rangingElapsedTimeText = findViewById(R.id.beacons_ranging_elapsed_time_text_view);
                        rangingElapsedTimeText.setText(beaconCollector.getRangingElapsedTime() + " ms");
                        if (Utilities.getUnixTimeMillis() > loggingStartTime + Integer.parseInt(beaconLogTimeEditText.getText().toString())) {
                            stopLogging();
                        }
                        if (beaconLogToggleButton.isChecked() && logger.isOpen()) {
                            for (BeaconWrapper beacon : beaconsArrayList) {
                                logger.log(beacon);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        if (beaconServiceConnected) {
            beaconCollector.startRanging();
        }
        updateLogging();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconServiceConnected = true;
        if (beaconCollector != null) {
            beaconCollector.startRanging();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLogging();
        beaconCollector.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionManager.REQUEST_MULTIPLE_PERMISSIONS:
                if (PermissionManager.werePermissionsGranted(grantResults)) {
                    Toast.makeText(getApplicationContext(), R.string.multiple_permission_allowed, Toast.LENGTH_SHORT).show();
                    updateLogging();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.multiple_permission_denied, Toast.LENGTH_SHORT).show();
                    updateLogging();
                }
                break;
            default:
                break;
        }
    }

    private void updateLogging() {
        if (permissionManager.hasPermissions(REQUIRED_PERMISSIONS)) {
            enableLogging();
        } else {
            enableLogging();
        }
    }

    private void enableLogging() {
        beaconLogToggleButton.setEnabled(true);
    }

    private void disableLogging() {
        beaconLogToggleButton.setEnabled(false);
    }

    private void startLogging(String logName) {
        try {
            logger.setFilename(logName);
            logger.open();
            loggingStartTime = Utilities.getUnixTimeMillis();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    private void stopLogging() {
        if (logger.isOpen()) {
            try {
                beaconLogToggleButton.setChecked(false);
                logger.close();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
    }
}
