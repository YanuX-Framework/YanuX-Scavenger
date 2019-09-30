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

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.nearby;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class NearbyConnectionsActivity extends AppCompatActivity {
    private static final String LOG_TAG = Constants.LOG_TAG + "_NEARBY_CON_ACTIVITY";
    private static final String SERVICE_ID = "pt.unl.fct.di.novalincs.yanux.scavenger";
    private final ConnectionLifecycleCallback mConnectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(@NonNull final String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(LOG_TAG, "Connection has been initiated on the " + endpointId + " endpoint");
                    // Automatically accept the connection on both sides.
                    //getConnectionsClient().acceptConnection(endpointId, mPayloadCallback);
                    new AlertDialog.Builder(NearbyConnectionsActivity.this)
                            .setTitle("Accept connection to " + connectionInfo.getEndpointName())
                            .setMessage("Confirm the code " + connectionInfo.getAuthenticationToken() + " is also displayed on the other device")
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The user confirmed, so we can accept the connection.
                                    getConnectionsClient().acceptConnection(endpointId, mPayloadCallback);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The user canceled, so we should reject the connection.
                                    getConnectionsClient().rejectConnection(endpointId);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

                @Override
                public void onConnectionResult(@NonNull String endpointId, ConnectionResolution result) {
                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            Log.i(LOG_TAG, "We're connected to the " + endpointId + " endpoint! We can now start sending and receiving data.");
                            sendMessage(endpointId, "Hello from " + getUserNickname());
                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            Log.i(LOG_TAG, "The connection was rejected by one or both sides of the " + endpointId + " endpoint");
                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            Log.i(LOG_TAG, "The connection broke before it was able to be accepted at the " + endpointId + " endpoint");
                            break;
                    }
                }

                @Override
                public void onDisconnected(@NonNull String endpointId) {
                    Log.i(LOG_TAG, "We've been disconnected from the " + endpointId + " endpoint. No more data can be sent or received.");
                }
            };
    private PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            if (payload.getType() == Payload.Type.BYTES) {
                Log.i(LOG_TAG, "Bytes from " + endpointId + " endpoint: " + new String(payload.asBytes()));
            } else if (payload.getType() == Payload.Type.FILE) {
                Log.i(LOG_TAG, "File from : " + endpointId + " endpoint");
            } else if (payload.getType() == Payload.Type.STREAM) {
                Log.i(LOG_TAG, "Stream from : " + endpointId + " endpoint");
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
            Log.i(LOG_TAG, "onPayloadTransferUpdate String: " + s + " PayloadTransferUpdate: " + payloadTransferUpdate.toString());
        }
    };
    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                @NonNull
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
                    Log.i(LOG_TAG, "The " + endpointId + " endpoint was found! DiscoveredEndpointInfo: " + discoveredEndpointInfo.toString());
                    getConnectionsClient().requestConnection(getUserNickname(), endpointId, mConnectionLifecycleCallback);
                }

                @Override
                @NonNull
                public void onEndpointLost(String endpointId) {
                    Log.i(LOG_TAG, "The previously discovered " + endpointId + " endpoint has gone away.");
                    getConnectionsClient().disconnectFromEndpoint(endpointId);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_nearby_connections);
        ((Switch) findViewById(R.id.nearby_connection_on_off)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CheckBox advertising = findViewById(R.id.nearby_connection_advertising);
                    CheckBox discovery = findViewById(R.id.nearby_connection_discovery);
                    if (advertising.isChecked()) {
                        startAdvertising();
                    }
                    if (discovery.isChecked()) {
                        startDiscovery();
                    }
                } else {
                    stopAdvertising();
                    stopDiscovery();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopAdvertising();
        stopDiscovery();
    }

    private void startAdvertising() {
        getConnectionsClient().startAdvertising(
                getUserNickname(),
                SERVICE_ID,
                mConnectionLifecycleCallback,
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build())
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                Log.i(LOG_TAG, "We're advertising!");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(LOG_TAG, "We were unable to start advertising.");
                            }
                        });
    }

    private void startDiscovery() {
        getConnectionsClient().startDiscovery(
                SERVICE_ID,
                mEndpointDiscoveryCallback,
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build())
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unusedResult) {
                                Log.i(LOG_TAG, "We're discovering!");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(LOG_TAG, "We were unable to start discovering.");
                            }
                        });
    }

    private String getUserNickname() {
        return ((EditText) findViewById(R.id.user_nickname)).getText().toString();
    }

    private void sendMessage(String endpointId, String message) {
        getConnectionsClient().sendPayload(endpointId, Payload.fromBytes(message.getBytes()));
    }

    private ConnectionsClient getConnectionsClient() {
        return Nearby.getConnectionsClient(this);
    }

    private void stopAdvertising() {
        getConnectionsClient().stopAdvertising();
    }

    private void stopDiscovery() {
        getConnectionsClient().stopDiscovery();
    }
}
