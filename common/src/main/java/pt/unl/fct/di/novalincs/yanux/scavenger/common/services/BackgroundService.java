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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fi.iki.elonen.NanoHTTPD;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class BackgroundService implements Service {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + BackgroundService.class.getSimpleName();
    private static final String REGION_UUID = "cc83a39c-075d-4f9d-b78a-a94d66d57b97";
    private static final String IBEACON_UUID = "113069EC-6E64-4BD3-6810-DE01B36E8A3E";
    private Preferences preferences;
    private BeaconCollector beaconCollector;
    private Context context;
    private BeaconConsumer beaconConsumer;
    private boolean beaconServiceConnected = false;

    public BackgroundService(BeaconConsumer beaconConsumer) {
        this.beaconConsumer = beaconConsumer;
        this.context = (Context) beaconConsumer;
    }

    public void start() {
        Log.d(LOG_TAG, "MobileService: Start");
        preferences = new Preferences(context);
        /* UUID Generation */
        String deviceUuid = preferences.getDeviceUuid();
        if (deviceUuid == null) {
            deviceUuid = UUID.randomUUID().toString();
            preferences.setDeviceUuid(deviceUuid);
            Log.d(LOG_TAG, "New Device UUID: " + deviceUuid);
        } else {
            Log.d(LOG_TAG, "Current Device UUID: " + deviceUuid);
        }
        /* HTTP Server */
        try {
            HTTPServer httpServer = new HTTPServer();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
        }
        /* BLE */
        beaconCollector = new BeaconCollector(beaconConsumer, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BeaconCollector.ACTION_BEACON_RANGE_BEACONS:
                        List<Beacon> beaconsArrayList = intent.getParcelableArrayListExtra(BeaconCollector.EXTRA_BEACONS);
                        for (Beacon b : beaconsArrayList) {
                            Log.d(LOG_TAG, "Beacon: " + b.toString());
                        }
                        Log.d(LOG_TAG, "Ranging Elapsed Time: " + beaconCollector.getRangingElapsedTime() + " ms");
                        break;
                    default:
                        break;
                }
            }
        });
        beaconCollector.setRegion(new Region(REGION_UUID, Identifier.parse(IBEACON_UUID), null, null));
        listenForBleBeacons();
    }

    public void stop() {
        Log.d(LOG_TAG, "MobileService: Stop");
        beaconCollector.unbind();
    }

    public void listenForBleBeacons() {
        if (isBeaconServiceConnected() && getBeaconCollector() != null) {
            beaconCollector.startRanging();
        }
    }

    public boolean isBeaconServiceConnected() {
        return beaconServiceConnected;
    }

    public void setBeaconServiceConnected(boolean beaconServiceConnected) {
        this.beaconServiceConnected = beaconServiceConnected;
    }

    public BeaconCollector getBeaconCollector() {
        return beaconCollector;
    }

    public static class HTTPServer extends NanoHTTPD {
        public HTTPServer() throws IOException {
            super(8080);
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String msg = "<html><body><h1>Hello server</h1>\n";
            Map<String, String> parms = session.getParms();
            if (parms.get("username") == null) {
                msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
            } else {
                msg += "<p>Hello, " + parms.get("username") + "!</p>";
            }
            return newFixedLengthResponse(msg + "</body></html>\n");
        }
    }
}
