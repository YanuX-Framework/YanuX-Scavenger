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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
    private OkHttpClient httpClient;
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
            if (preferences.isPersistentServiceAllowed()) {
                if (!started) {
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
                    /* HTTP Client */
                    httpClient = new OkHttpClient();
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
                    /* YanuX Auth */
                    userAuthorization();
                    /* Socket.io -> YanuX Broker */
                    socket = IO.socket(preferences.getYanuxBrokerUrl());
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
                    authenticate();
                    // Mark the service as started.
                    started = true;
                } else {
                    Log.d(LOG_TAG, "MobileService: Already Started");
                }
            } else {
                Log.d(LOG_TAG, "MobileService: Disabled");
                stop();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            stop();
        }
    }

    private void userAuthorization() {
        if (preferences.getYanuxAuthJwt() == Preferences.INVALID
                && preferences.getYanuxAuthAccessToken() == Preferences.INVALID
                && preferences.getYanuxAuthRefreshToken() == Preferences.INVALID
                && preferences.getYanuxAuthAuthorizationCode() == Preferences.INVALID) {
            Toast.makeText(context, R.string.persistent_service_authentication_warning, Toast.LENGTH_LONG).show();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(preferences.getYanuxAuthOauth2AuthorizationServerUrl()
                            + "oauth2/authorize?client_id="
                            + preferences.getYanuxAuthClientId()
                            + "&response_type=code&redirect_uri="
                            + preferences.getYanuxAuthRedirectUri()));
            context.startActivity(browserIntent);
        }
    }

    private void authenticate() throws JSONException {
        JSONObject auth = new JSONObject();
        if (preferences.getYanuxAuthJwt() != Preferences.INVALID) {
            auth.put("strategy", "jwt");
            auth.put("accessToken", preferences.getYanuxAuthJwt());
        } else if (preferences.getYanuxAuthAccessToken() != Preferences.INVALID) {
            auth.put("strategy", "yanux");
            auth.put("clientId", preferences.getYanuxAuthClientId());
            auth.put("accessToken", preferences.getYanuxAuthAccessToken());
        } else {
            return;
        }
        socket.emit("authenticate", auth, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject message = (JSONObject) args[1];
                Log.d(LOG_TAG, "Message: " + message);
            }
        });
    }

    public void exchangeAuthorizationCode() {
        if (preferences.getYanuxAuthAuthorizationCode() != Preferences.INVALID) {
            Log.d(LOG_TAG, "Exchanging Authorization Code: " + preferences.getYanuxAuthAuthorizationCode());
            String credentials = Credentials.basic(preferences.getYanuxAuthClientId(), preferences.getYanuxAuthClientSecret());
            RequestBody requestBody = new FormBody.Builder()
                    .add("code", preferences.getYanuxAuthAuthorizationCode())
                    .add("grant_type", "authorization_code")
                    .add("redirect_uri", preferences.getYanuxAuthRedirectUri())
                    .build();
            Request request = new Request.Builder()
                    .url(preferences.getYanuxAuthOauth2AuthorizationServerUrl() + "oauth2/token")
                    .header("Authorization", credentials)
                    .post(requestBody)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(LOG_TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        preferences.setYanuxAuthAuthorizationCode(Preferences.INVALID);
                        userAuthorization();
                        IOException ioException = new IOException("Unexpected Code: " + response);
                        Log.e(LOG_TAG, ioException.toString());
                        throw ioException;
                    } else {
                        try {
                            JSONObject tokens = new JSONObject(response.body().string());
                            String accessToken = tokens.getString("access_token");
                            String refreshToken = tokens.getString("refresh_token");
                            Log.d(LOG_TAG, "Retrieved Access Token: " + accessToken + " and Refresh Token: " + refreshToken);
                            preferences.setPreferenceYanuxAccessToken(accessToken);
                            preferences.setPreferenceYanuxRefreshToken(refreshToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(LOG_TAG, e.toString());
                        }
                    }
                }
            });
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