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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

import org.altbeacon.beacon.Identifier;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.socket.client.Ack;
import io.socket.client.Socket;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconWrapper;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.YanuxBrokerBeacon;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Utilities;

public class PersistentServiceBeaconScanner extends BroadcastReceiver {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + PersistentServiceBeaconScanner.class.getSimpleName();

    private final PersistentService service;
    private final ObjectMapper objectMapper;
    private final Map<String, YanuxBrokerBeacon> beaconsCreated;
    private final Map<String, YanuxBrokerBeacon> beaconsUpdated;
    private final Set<String> beaconsToRemove;
    private final Handler beaconRefreshHandler;
    private final Runnable beaconRefresher;
    private int refreshInterval;
    private int inactivityTimer;

    public PersistentServiceBeaconScanner(PersistentService service) {
        this.service = service;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JsonOrgModule());
        this.beaconsCreated = new HashMap<>();
        this.beaconsUpdated = new HashMap<>();
        this.beaconsToRemove = new HashSet<>();
        this.beaconRefreshHandler = new Handler();
        this.beaconRefresher = new BeaconRefresherRunnable();
    }

    public void start(int refreshInterval, int inactivityTimer) {
        this.refreshInterval = refreshInterval;
        this.inactivityTimer = inactivityTimer;
        this.beaconRefreshHandler.postDelayed(beaconRefresher, this.refreshInterval);
    }

    public void stop() {
        this.beaconRefreshHandler.removeCallbacks(beaconRefresher);
    }

    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BeaconCollector.ACTION_BEACON_RANGE_BEACONS:
                if (service.getBeaconCollector() != null) {
                    List<BeaconWrapper> beaconsArrayList = intent.getParcelableArrayListExtra(BeaconCollector.EXTRA_BEACONS);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (BeaconWrapper b : beaconsArrayList) {
                        stringBuilder.append("\n");
                        stringBuilder.append(b.toString());
                    }
                    Log.d(LOG_TAG, //"\n" + "Beacons: " + stringBuilder + "\n" +
                            "Ranging Elapsed Time: " + service.getBeaconCollector().getRangingElapsedTime() + " ms");
                    update(beaconsArrayList);
                }
                break;
            default:
                break;
        }
    }

    private void update(List<BeaconWrapper> beacons) {
        long unixTime = Utilities.getUnixTimeMillis();
        Socket socket = service.getSocket();
        String userId = service.getUserId();
        String deviceUuid = service.getPreferences().getDeviceUuid();
        if (socket != null && !userId.isEmpty() && !deviceUuid.isEmpty()) {
            for (BeaconWrapper beacon : beacons) {
                String address = beacon.getBluetoothAddress().replace(":", "").toLowerCase();
                StringBuilder beaconKeyBuilder = new StringBuilder();
                beaconKeyBuilder.append(address);
                beaconKeyBuilder.append("-");
                beaconKeyBuilder.append(beacon.getParserIdentifier());
                List<Object> identifiers = new ArrayList<>();
                for (Identifier identifier : beacon.getIdentifiers()) {
                    Object currentIdentifier;
                    try {
                        currentIdentifier = identifier.toInt();
                    } catch (UnsupportedOperationException e) {
                        currentIdentifier = identifier.toString().toUpperCase();
                    }
                    identifiers.add(currentIdentifier);
                    beaconKeyBuilder.append("-");
                    beaconKeyBuilder.append(currentIdentifier);
                }
                String beaconKey = beaconKeyBuilder.toString();
                YanuxBrokerBeacon beaconObject = new YanuxBrokerBeacon(userId, deviceUuid, beaconKey,
                        address, beacon.getParserIdentifier(),
                        identifiers, beacon.getTxPower(), beacon.getRssi(),
                        unixTime);
                JSONObject beaconJson = objectMapper.convertValue(beaconObject, JSONObject.class);
                if (beaconsCreated.containsKey(beaconKey) || beaconsUpdated.containsKey(beaconKey)) {
                    beaconsCreated.remove(beaconKey);
                    beaconsUpdated.put(beaconKey, beaconObject);
                    try {
                        JSONObject query = new JSONObject();
                        query.put("user", userId);
                        query.put("deviceUuid", deviceUuid);
                        query.put("beaconKey", beaconKey);
                        socket.emit("patch", "beacons", null, beaconJson, query, new Ack() {
                            @Override
                            public void call(Object... args) {
                                if (args[0] == null) {
                                    Log.d(LOG_TAG, "Beacon Updated: " + args[1]);
                                } else {
                                    service.handleError(args[0]);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Could not update beacon: " + e.toString());
                    }
                } else {
                    beaconsCreated.put(beaconKey, beaconObject);
                    socket.emit("create", "beacons", beaconJson, new Ack() {
                        @Override
                        public void call(Object... args) {
                            if (args[0] == null) {
                                Log.d(LOG_TAG, "Beacon Created: " + args[1]);
                            } else service.handleError(args[0]);
                        }
                    });
                }
            }
        }
    }

    private class BeaconRefresherRunnable implements Runnable {
        @Override
        public void run() {
            Socket socket = service.getSocket();
            String userId = service.getUserId();
            String deviceUuid = service.getPreferences().getDeviceUuid();
            if (socket != null && !userId.isEmpty() && !deviceUuid.isEmpty()) {
                Log.d(LOG_TAG, "Refreshing beacons list");
                long unixTime = Utilities.getUnixTimeMillis();
                for (Map.Entry<String, YanuxBrokerBeacon> entry : beaconsCreated.entrySet()) {
                    if (unixTime - entry.getValue().getBeacon().getTimestamp() > inactivityTimer) {
                        beaconsToRemove.add(entry.getKey());
                    }
                }
                for (Map.Entry<String, YanuxBrokerBeacon> entry : beaconsUpdated.entrySet()) {
                    if (unixTime - entry.getValue().getBeacon().getTimestamp() > inactivityTimer) {
                        beaconsToRemove.add(entry.getKey());
                    }
                }
                Iterator<String> beaconsToRemoveIt = beaconsToRemove.iterator();
                while (beaconsToRemoveIt.hasNext()) {
                    String beaconKey = beaconsToRemoveIt.next();
                    try {
                        JSONObject query = new JSONObject();
                        query.put("user", userId);
                        query.put("deviceUuid", deviceUuid);
                        query.put("beaconKey", beaconKey);
                        socket.emit("remove", "beacons", null, query, new Ack() {
                            @Override
                            public void call(Object... args) {
                                if (args[0] == null) {
                                    Log.d(LOG_TAG, "Beacon Removed: " + args[1]);
                                } else service.handleError(args[0]);
                            }
                        });
                        beaconsToRemoveIt.remove();
                        beaconsCreated.remove(beaconKey);
                        beaconsUpdated.remove(beaconKey);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Could not remove beacon: " + e.toString());
                    }
                }
            }
            beaconRefreshHandler.postDelayed(beaconRefresher, refreshInterval);
        }
    }
}
