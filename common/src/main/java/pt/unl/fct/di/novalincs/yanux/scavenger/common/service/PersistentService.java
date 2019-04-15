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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import fi.iki.elonen.NanoHTTPD;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconAdvertiser;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.EncryptionToolbox;

public class PersistentService implements GenericService {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + PersistentService.class.getSimpleName();
    private static final String REGION_UUID = "cc83a39c-075d-4f9d-b78a-a94d66d57b97";

    private final BeaconConsumer beaconConsumer;
    private final Context context;
    private final Handler mainHandler;
    private final SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private final Preferences preferences;
    private final PersistentServiceZeroconf zeroconf;
    private final PersistentServiceBeaconScanner beaconScanner;
    private final BeaconCollector beaconCollector;
    private final BeaconAdvertiser beaconAdvertiser;
    private final PersistentServiceHTTPServer httpServer;
    private final OkHttpClient httpClient;
    private Socket socket;
    private JSONObject user;
    private boolean started;

    public PersistentService(BeaconConsumer beaconConsumer) {
        this.beaconConsumer = beaconConsumer;
        this.context = (Context) beaconConsumer;
        this.mainHandler = new PersistentServiceMainLooperHandler(context);
        this.sharedPreferenceChangeListener = new PersistentServiceSharedPreferenceChangeListener(this);
        this.preferences = new Preferences(context);
        this.zeroconf = new PersistentServiceZeroconf(this.context);
        this.beaconScanner = new PersistentServiceBeaconScanner(this);
        this.beaconCollector = new BeaconCollector(beaconConsumer, this.beaconScanner);
        this.beaconAdvertiser = new BeaconAdvertiser(context);
        this.httpServer = new PersistentServiceHTTPServer(context, preferences.getHttpServerPort());
        this.httpClient = new OkHttpClient();
        this.started = false;
    }

    public void start() {
        try {
            if (preferences.isPersistentServiceAllowed()) {
                if (preferences.isZeroconfAllowed()) {
                    zeroconf.startDiscovery();
                }
                if (!started) {
                    Log.d(LOG_TAG, "MobileService: Start");
                    /* UUID Generation */
                    String deviceUuid = preferences.getDeviceUuid();
                    if (deviceUuid.isEmpty()) {
                        deviceUuid = UUID.randomUUID().toString();
                        preferences.setDeviceUuid(deviceUuid);
                        Log.d(LOG_TAG, "New Device UUID: " + deviceUuid);
                    } else {
                        Log.d(LOG_TAG, "Current Device UUID: " + deviceUuid);
                    }
                    /* HTTP Server */
                    httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
                    /* Socket.io -> YanuX Broker */
                    String yanuxBrokerUrl = preferences.getYanuxBrokerUrl();
                    socket = IO.socket(preferences.getYanuxBrokerUrl());
                    socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker CONNECT");
                        }
                    }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_CONNECT_TIMEOUT");
                        }
                    }).on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_CONNECTING");
                        }
                    }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_CONNECT_ERROR");
                        }
                    }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_DISCONNECT");
                        }
                    }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_ERROR");
                        }
                    }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_MESSAGE");
                        }
                    }).on(Socket.EVENT_PING, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_PING");
                        }
                    }).on(Socket.EVENT_PONG, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_PONG");
                        }
                    }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_RECONNECT");
                        }
                    }).on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_RECONNECT_ATTEMPT");
                        }
                    }).on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_RECONNECT_ERROR");
                        }
                    }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_RECONNECT_FAILED");
                        }
                    }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: YanuX Broker EVENT_RECONNECTING");
                        }
                    })/*.on("beacons created", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, args[0].toString());
                        }
                    }).on("beacons patched", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, args[0].toString());
                        }
                    }).on("beacons removed", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, args[0].toString());
                        }
                    })*/.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d(LOG_TAG, "MobileService: Disconnected from YanuX Broker.");
                        }
                    });
                    socket.connect();
                    authenticate();
                    /* BLE */
                    startBeaconScan();
                    startBeaconAdvertisement();
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
            Log.e(LOG_TAG, "MobileService: Exception: " + e.toString());
            stop();
        }
    }

    public void stop() {
        Log.d(LOG_TAG, "MobileService: Stop");
        zeroconf.stopDiscovery();
        stopBeaconScan();
        stopBeaconAdvertisement();
        httpServer.stop();
        if (socket != null) {
            tidyUpBeacons();
            socket.disconnect();
        }
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public void registerSharedPreferenceChangeListener() {
        this.preferences.getPreferences().registerOnSharedPreferenceChangeListener(this.sharedPreferenceChangeListener);
    }

    public void unregisterSharedPreferenceChangeListener() {
        this.preferences.getPreferences().unregisterOnSharedPreferenceChangeListener(this.sharedPreferenceChangeListener);
    }

    private void authenticate() throws JSONException {
        JSONObject auth = new JSONObject();
        if (!preferences.getYanuxAuthJwt().isEmpty()) {
            auth.put("strategy", "jwt");
            auth.put("accessToken", preferences.getYanuxAuthJwt());
        } else if (!preferences.getYanuxAuthAccessToken().isEmpty()) {
            auth.put("strategy", "yanux");
            auth.put("clientId", preferences.getYanuxAuthClientId());
            auth.put("accessToken", preferences.getYanuxAuthAccessToken());
        } else {
            userAuthorization();
        }
        socket.emit("authenticate", auth, new Ack() {
            @Override
            public void call(Object... args) {
                if (args[0] == null) {
                    JSONObject data = (JSONObject) args[1];
                    Log.d(LOG_TAG, "Data: " + data);
                    Jws<Claims> jwt;
                    try {
                        jwt = Jwts.parser()
                                .setSigningKey(EncryptionToolbox.getPublicKey(context))
                                .parseClaimsJws(data.getString("accessToken"));
                        String userId = (String) jwt.getBody().get("userId");
                        Log.d(LOG_TAG, "userId: " + userId);
                        socket.emit("get", "users", userId, new Ack() {
                            @Override
                            public void call(Object... args) {
                                if (args[0] == null) {
                                    user = (JSONObject) args[1];
                                    tidyUpBeacons();
                                    registerDevices();
                                } else handleError(args[0]);
                            }
                        });
                    } catch (JwtException ex) {
                        Log.e(LOG_TAG, "The JWT is not valid: " + ex.toString());
                        ex.printStackTrace();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.toString());
                    }
                } else handleError(args[0]);
            }
        });
    }

    public void userAuthorization() {
        if (preferences.getYanuxAuthAccessToken().isEmpty()
                || preferences.getYanuxAuthRefreshToken().isEmpty()) {
            Message message = mainHandler.obtainMessage(PersistentServiceMainLooperHandler.HANDLE_SHOW_TOAST, context.getString(R.string.persistent_service_authentication_warning));
            message.sendToTarget();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(preferences.getYanuxAuthOauth2AuthorizationServerUrl()
                            + "oauth2/authorize?client_id="
                            + preferences.getYanuxAuthClientId()
                            + "&response_type=code&redirect_uri="
                            + preferences.getYanuxAuthRedirectUri()));
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browserIntent);
        }
    }

    public void exchangeAuthorizationCode() {
        if (!preferences.getYanuxAuthAuthorizationCode().isEmpty()) {
            Log.d(LOG_TAG, "Exchanging Authorization Code: " + preferences.getYanuxAuthAuthorizationCode());
            RequestBody requestBody = new FormBody.Builder()
                    .add("code", preferences.getYanuxAuthAuthorizationCode())
                    .add("grant_type", "authorization_code")
                    .add("redirect_uri", preferences.getYanuxAuthRedirectUri())
                    .build();
            makeOAuth2TokenRequest(requestBody, true);
        }
    }

    private void exchangeRefreshToken() {
        if (!preferences.getYanuxAuthRefreshToken().isEmpty()) {
            Log.d(LOG_TAG, "Trying to get a new token using the Refresh Token");
            String credentials = Credentials.basic(preferences.getYanuxAuthClientId(), preferences.getYanuxAuthClientSecret());
            RequestBody requestBody = new FormBody.Builder()
                    .add("refresh_token", preferences.getYanuxAuthRefreshToken())
                    .add("grant_type", "refresh_token")
                    .add("redirect_uri", preferences.getYanuxAuthRedirectUri())
                    .build();
            makeOAuth2TokenRequest(requestBody, true);
        }
    }

    private void makeOAuth2TokenRequest(RequestBody requestBody, boolean reauthenticate) {
        if (!preferences.getYanuxAuthOauth2AuthorizationServerUrl().isEmpty()) {
            String credentials = Credentials.basic(preferences.getYanuxAuthClientId(), preferences.getYanuxAuthClientSecret());
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
                        Log.e(LOG_TAG, "OAuth 2.0 Token Exchange Failed: " + response);
                        Log.d(LOG_TAG, "Asking the user to re-authenticate");
                        clearTokens();
                        userAuthorization();
                    } else {
                        try {
                            JSONObject tokens = new JSONObject(response.body().string());
                            String accessToken = tokens.getString("access_token");
                            String refreshToken = tokens.getString("refresh_token");
                            Log.d(LOG_TAG, "Retrieved Access Token: " + accessToken + " and Refresh Token: " + refreshToken);
                            preferences.setYanuxAuthAuthorizationCode(Preferences.EMPTY);
                            preferences.setYanuxAccessToken(accessToken);
                            preferences.setYanuxRefreshToken(refreshToken);
                            if (reauthenticate) {
                                authenticate();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(LOG_TAG, e.toString());
                        }
                    }
                }
            });
        }
    }

    private void clearTokens() {
        preferences.setYanuxAuthAuthorizationCode(Preferences.EMPTY);
        preferences.setYanuxAccessToken(Preferences.EMPTY);
        preferences.setYanuxRefreshToken(Preferences.EMPTY);
        preferences.setYanuxAuthJwt(Preferences.EMPTY);
    }

    public void startBeaconScan() {
        if (preferences.isPersistentServiceAllowed() && preferences.shouldBeaconScan()) {
            beaconScanner.start(preferences.getBeaconsRefreshInterval(), preferences.getBeaconsInactivityTimer());
            beaconCollector.bind();
            String uuid = preferences.getBeaconMatcherParametersUuid();
            int major = preferences.getBeaconMatcherParametersMajor();
            int minor = preferences.getBeaconMatcherParametersMinor();
            beaconCollector.setRegion(new Region(REGION_UUID,
                    Identifier.parse(uuid),
                    major > 0 ? Identifier.fromInt(major) : null,
                    minor > 0 ? Identifier.fromInt(minor) : null));
            beaconCollector.startRanging();
        }
    }

    public void stopBeaconScan() {
        beaconScanner.stop();
        beaconCollector.stopRanging();
        beaconCollector.unbind();
    }

    public void startBeaconAdvertisement() {
        if (preferences.isPersistentServiceAllowed() && preferences.shouldBeaconAdvertise()) {
            beaconAdvertiser.start();
        }
    }

    public void stopBeaconAdvertisement() {
        beaconAdvertiser.stop();
    }

    private void tidyUpBeacons() {
        try {
            JSONObject query = new JSONObject();
            query.put("user", getUserId());
            query.put("deviceUuid", preferences.getDeviceUuid());
            JSONObject params = new JSONObject();
            params.put("query", query);
            socket.emit("remove", "beacons", null, params, new Ack() {
                @Override
                public void call(Object... args) {
                    if (args[0] == null) {
                        Log.d(LOG_TAG, "Removed outstanding beacons: " + args[1]);
                    } else handleError(args[0]);
                }
            });
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    private void registerDevices() {
        try {
            JSONObject deviceJSON = new JSONObject();
            deviceJSON.put("deviceUuid", preferences.getDeviceUuid());

            JSONArray beaconValues = new JSONArray();
            beaconValues.put(preferences.getBeaconAdvertiserParametersUuid());
            beaconValues.put(preferences.getBeaconAdvertiserParametersMajor());
            beaconValues.put(preferences.getBeaconAdvertiserParametersMinor());

            deviceJSON.put("beaconValues", beaconValues);

            JSONObject capabilities = new JSONObject();
            capabilities.put("view", preferences.hasViewCapabilities());
            capabilities.put("control", preferences.hasControlCapabilities());

            deviceJSON.put("capabilities", capabilities);

            JSONObject query = new JSONObject();
            query.put("deviceUuid", preferences.getDeviceUuid());
            socket.emit("patch", "devices", null, deviceJSON, query, new Ack() {
                @Override
                public void call(Object... args) {
                    if (args[0] == null) {
                        Log.d(LOG_TAG, "Registered Device: " + args[1]);
                    } else handleError(args[0]);
                }
            });
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    public BeaconCollector getBeaconCollector() {
        return beaconCollector;
    }

    public String getUserId() {
        try {
            if (user != null) {
                return user.getString("_id");
            } else {
                return "";
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.toString());
            return "";
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public PersistentServiceZeroconf getZeroconf() {
        return zeroconf;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void handleError(Object error) {
        Log.e(LOG_TAG, "Error: " + error.toString());
        if (error instanceof JSONObject) {
            try {
                JSONObject jsonError = (JSONObject) error;
                String errorMessage = jsonError.has("message") ? jsonError.getString("message") : null;
                String errorName = jsonError.has("name") ? jsonError.getString("name") : null;
                if ("NotAuthenticated".equals(errorName)) {
                    if ("jwt expired".equals(errorMessage)) {
                        preferences.setYanuxAuthJwt(Preferences.EMPTY);
                    } else if (!"No auth token".equals(errorMessage)) {
                        authenticate();
                    }
                } else if ("The provided access token is not valid.".equals(errorMessage)) {
                    exchangeRefreshToken();
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
    }
}
