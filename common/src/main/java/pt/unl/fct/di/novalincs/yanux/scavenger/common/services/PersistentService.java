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
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.List;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class PersistentService implements Service {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + PersistentService.class.getSimpleName();
    private static final String REGION_UUID = "cc83a39c-075d-4f9d-b78a-a94d66d57b97";
    private static final String IBEACON_UUID = "113069EC-6E64-4BD3-6810-DE01B36E8A3E";
    private Preferences preferences;
    private Context context;
    private BeaconCollector beaconCollector;
    private BeaconConsumer beaconConsumer;
    private HTTPServer httpServer;
    private Socket socket;
    private boolean started;
    private boolean beaconServiceConnected = false;

    public PersistentService(BeaconConsumer beaconConsumer) {
        this.beaconConsumer = beaconConsumer;
        this.context = (Context) beaconConsumer;
    }

    public void start() {
        try {
            preferences = new Preferences(context);
            if (!started && preferences.isPersistentServiceAllowed()) {
                Log.d(LOG_TAG, "MobileService: Start");
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
                httpServer = new HTTPServer();
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
                /* Socket.io -> YanuX Broker */
                socket = IO.socket(preferences.getYanuxBrokerUrl());
                if (preferences.getYanuxAuthJwt() == Preferences.INVALID
                        && preferences.getYanuxAuthAccessToken() == Preferences.INVALID
                        && preferences.getYanuxAuthRefreshToken() == Preferences.INVALID) {
                    Toast.makeText(context, R.string.persistent_service_authentication_warning, Toast.LENGTH_LONG).show();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(preferences.getYanuxAuthOauth2AuthorizationServerUrl()
                                    + "oauth2/authorize?client_id="
                                    + preferences.getYanuxAuthClientId()
                                    + "&response_type=code&redirect_uri="
                                    + preferences.getYanuxAuthRedirectUri()));
                    context.startActivity(browserIntent);
                }
                /*
                JSONObject obj = new JSONObject();
                try {
                    obj.put("strategy", "yanux");
                    obj.put("client_id", preferences.getYanuxAuthClientId());
                    //TODO: Retrieve and add the access_token here!
                    obj.put("access_token", "********");
                    //TODO: Add Access Token.
                    socket.emit("authenticate", obj, new Ack() {
                        @Override
                        public void call(Object... args) {
                            JSONObject message = (JSONObject) args[0];
                            Log.d(LOG_TAG, "Message: " + message);
                        }
                    });
                } catch (JSONException e) { Log.e(LOG_TAG, e.toString()); }
                */
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d(LOG_TAG, "MobileService: Connected to YanuX Broker.");
                    }
                }).on("event", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                    }
                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d(LOG_TAG, "MobileService: Disconnected from YanuX Broker.");
                    }
                });
                socket.connect();
                // Mark the service as started.
                started = true;
            } else {
                Log.d(LOG_TAG, "MobileService: Disabled");
                stop();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            stop();
        }
    }

    public void stop() {
        Log.d(LOG_TAG, "MobileService: Stop");
        if (getBeaconCollector() != null) {
            if (started) {
                beaconCollector.stopRanging();
            }
            beaconCollector.unbind();
            setBeaconServiceConnected(false);
        }
        if (httpServer != null) {
            httpServer.stop();
        }
        if (socket != null) {
            socket.disconnect();
        }
        started = false;
    }

    public void listenForBleBeacons() {
        if (isBeaconServiceConnected() && getBeaconCollector() != null) {
            beaconCollector.startRanging();
        }
    }

    public boolean isStarted() {
        return started;
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
}
