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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.services;

import android.os.Handler;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

import org.altbeacon.beacon.Beacon;
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
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Utilities;

public class BeaconScanner {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + PersistentService.class.getSimpleName();

    private final Service service;
    private final Socket socket;
    private final ObjectMapper objectMapper;
    private final Map<String, BeaconPOJO> beaconsCreated;
    private final Map<String, BeaconPOJO> beaconsUpdated;
    private final Set<String> beaconsToRemove;
    private String userId;
    private String deviceUuid;

    public BeaconScanner(Service service, Socket socket, int refreshInterval, int inactivityTimer) {
        this.service = service;
        this.socket = socket;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonOrgModule());
        beaconsCreated = new HashMap<>();
        beaconsUpdated = new HashMap<>();
        beaconsToRemove = new HashSet<>();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (userId != null && deviceUuid != null) {
                    Log.d(LOG_TAG, "Refreshing beacons list");
                    long unixTime = Utilities.getUnixTimeMillis();
                    for (Map.Entry<String, BeaconPOJO> entry : beaconsCreated.entrySet()) {
                        if (unixTime - entry.getValue().getBeacon().getTimestamp() > inactivityTimer) {
                            beaconsToRemove.add(entry.getKey());
                        }
                    }
                    for (Map.Entry<String, BeaconPOJO> entry : beaconsUpdated.entrySet()) {
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
                                    } else {
                                        service.handleError(args[0]);
                                    }
                                }
                            });
                            beaconsToRemoveIt.remove();
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Could not remove beacon: " + e.toString());
                        }

                    }
                }
                handler.postDelayed(this, refreshInterval);
            }
        }, refreshInterval);
    }

    public void update(String userId, String deviceUuid, List<Beacon> beacons) {
        this.userId = userId;
        this.deviceUuid = deviceUuid;
        long unixTime = Utilities.getUnixTimeMillis();
        for (Beacon beacon : beacons) {
            String address = beacon.getBluetoothAddress().replace(":", "").toLowerCase();
            StringBuilder beaconKeyBuilder = new StringBuilder();
            beaconKeyBuilder.append(address);
            beaconKeyBuilder.append("-");
            beaconKeyBuilder.append(beacon.getParserIdentifier());
            List<Object> identifiers = new ArrayList<>();
            for (Identifier identifier : beacon.getIdentifiers()) {
                Object currentIdentifier = null;
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
            BeaconPOJO beaconObject = new BeaconPOJO(userId, deviceUuid, beaconKey,
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
                        } else {
                            service.handleError(args[0]);
                        }
                    }
                });
            }
        }
    }
}
