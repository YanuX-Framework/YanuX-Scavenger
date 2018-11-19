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

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.nearby;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.BleSignal;
import com.google.android.gms.nearby.messages.Distance;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import androidx.appcompat.app.AppCompatActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

/*
 * TODO:
 * Refactor all the code below so that all non mobile (smartphone/tablet) parts are placed in the
 * project's common module.
 */
public class NearbyMessagesActivity extends AppCompatActivity {
    private static final String LOG_TAG = Constants.LOG_TAG + "_NEARBY_MSG_ACTIVITY";

    private MessageListener mMessageListener;
    private Message mMessage;
    private PendingIntent mBackgroundReceiverPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_nearby_messages);
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d(LOG_TAG, "Found Message" +
                        " Type:" + message.getType() +
                        " Namespace: " + message.getNamespace() +
                        " Content: " + new String(message.getContent()));
            }

            @Override
            public void onLost(Message message) {
                Log.d(LOG_TAG, "Lost Message" +
                        " Type:" + message.getType() +
                        " Namespace: " + message.getNamespace() +
                        " Content: " + new String(message.getContent()));
            }

            @Override
            public void onBleSignalChanged(Message message, BleSignal bleSignal) {
                Log.d(LOG_TAG, "BLE Signal Changed Message" +
                        " Type:" + message.getType() +
                        " Namespace: " + message.getNamespace() +
                        " Content: " + new String(message.getContent()) +
                        " RSSI: " + bleSignal.getRssi() +
                        " Tx Power: " + bleSignal.getTxPower());
            }

            @Override
            public void onDistanceChanged(Message message, Distance distance) {
                Log.d(LOG_TAG, "BLE Signal Changed Message" +
                        " Type:" + message.getType() +
                        " Namespace: " + message.getNamespace() +
                        " Content: " + new String(message.getContent()) +
                        " Distance: " + distance.getMeters() +
                        " Accuracy: " + distance.getAccuracy());
            }
        };
        mMessage = new Message("{\"message\":\"Hello from YanuX Scavenger\"}".getBytes());
        /*
         * NOTE:
         * Source: https://developers.google.com/nearby/messages/android/user-consent
         */
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMessagesClient = Nearby.getMessagesClient(this, new MessagesOptions.Builder().setPermissions(NearbyPermissions.BLE).build());
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribe();
        publish();
    }

    @Override
    public void onStop() {
        super.onStop();
        backgroundSubscribe();
        unsubscribe();
        unpublish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        backgroundUnsubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundUnsubscribe();
    }

    /* NOTE:
     * This whole Nearby Messages by Google is just a mess. The documentation recommends that you use
     * DISCOVERY_MODE_BROADCAST for Publishing and DISCOVERY_MODE_SCAN for Subscribing, but if I do the
     * devices will just not detect each other if I also use DISTANCE_TYPE_EARSHOT to limit the distance
     * to just a few meters. I thought that the problem could lie in the fact that I was publishing and
     * subscribing at the same time, but I have also tried to compile a version of the app that just
     * subscribes and a version of the app that just publishes and I still get the same results.
     *
     * After some careful testing using some audio analysis apps I think that part of the problem may
     * be that the Nexus 5 that I'm using as one of my testing devices has a microphone that is not
     * very sensitive at those frequencies. I might have to retest this approach with a few other
     * devices just to be 100% sure.
     *
     * Nevertheless, there are a few parameter combinations that allow that the devices to detect
     * each other. I say a few because I've been unable to correctly determine the ones that work reliably
     * because it's just a nightmare to test all the combinations. Not that there are that many combinations.
     * The problem lies in that the API doesn't seem to actually want to detect devices in real-time.
     * For instance, I test with settings that don't work, switch to settings that work, than I switch
     * back to the settings that don't work and they suddenly work. However, if I wait 10 minutes or so,
     * they stop working again. It seems that the detection is simply cached by the API backend and
     * whenever the app subscribes/publishes to the API the server simulates that the devices are
     * nearby as long has they have seen each other not too long ago.
     *
     * The problem with this approach is that:
     * 1. It makes it nearly impossible to systematically test the API behavior. Which shouldn't even
     * be needed in the first place if the documentation was 100% clear/matched the observed behavior.
     * 2. Unless I'm completely wrong about the detection cache, it is basically useless for the overall
     * YanuX Framework because even if the API can quickly detect a device it will not forget about it
     * in a very long time.
     *
     * However, I should say that the beacon detection part is actually more robust than the device
     * to device detection. I haven't tested the background subscribing part, but I should say that
     * beacon detection in the foreground works much more consistently than the detection of other
     * devices. In fact, it is possible to get an idea of how long have you been away from a given
     * beacon because of the onBleSignalChanged/onDistanceChanged methods of the MessageListener class
     * implemented above. I'm not sure if these methods are only ever called for BLE beacons, but I
     * have only seen them reliably working in those cases. Moreover, it is also possible to get
     * more information about each of your Beacons as described here:
     * - https://developers.google.com/nearby/messages/android/get-beacon-messages
     *
     * It is also worth mentioning that you can use the Google Beacon Tools app
     * (https://play.google.com/store/apps/details?id=com.google.android.apps.location.beacon.beacontools)
     * and the Google Beacon Platform Dashboard (https://developers.google.com/beacons/dashboard/)
     * to register and manage beacons in the Google Cloud Platform ecosystem, which also include the
     * Google Proximity Beacon API: https://developers.google.com/beacons/proximity/guides
     *
     * P.S.: Crazy idea: Use the devices as beacons that can be added to the ecosystem. Could this be
     * a loophole that allows me to use Google Nearby Messages API according to my tastes? Hmmm...
     * Think about it!
     */
    private void publish() {
        Log.i(LOG_TAG, "Publishing");
        Strategy strategy = new Strategy.Builder()
                //.setDiscoveryMode(Strategy.DISCOVERY_MODE_BROADCAST)
                //.setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
                //.setTtlSeconds(Strategy.TTL_SECONDS_MAX)
                .build();
        Nearby.getMessagesClient(this).publish(mMessage,
                new PublishOptions.Builder()
                        .setStrategy(strategy)
                        .setCallback(new PublishCallback() {
                            @Override
                            public void onExpired() {
                                super.onExpired();
                                Log.i(LOG_TAG, "Publishing Expired");
                            }
                        }).build());
    }

    private void subscribe() {
        Log.i(LOG_TAG, "Subscribing");
        Strategy strategy = new Strategy.Builder()
                //.setDiscoveryMode(Strategy.DISCOVERY_MODE_SCAN)
                //.setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
                //.setTtlSeconds(Strategy.TTL_SECONDS_MAX)
                .build();
        Nearby.getMessagesClient(this).subscribe(mMessageListener,
                new SubscribeOptions.Builder()
                        .setStrategy(strategy)
                        .setCallback(new SubscribeCallback() {
                            @Override
                            public void onExpired() {
                                super.onExpired();
                                Log.i(LOG_TAG, "Subscribing Expired");
                            }
                        }).build());
    }

    private void unsubscribe() {
        Log.i(LOG_TAG, "Unsubscribing");
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
    }

    private void unpublish() {
        Log.i(LOG_TAG, "Unpublishing");
        Nearby.getMessagesClient(this).unpublish(mMessage);
    }

    // Subscribe to messages in the background. NOTE: [[UNTESTED]]
    private void backgroundSubscribe() {
        Log.i(LOG_TAG, "Background Subscribing");
        mBackgroundReceiverPendingIntent = getPendingIntent();
        Nearby.getMessagesClient(this).subscribe(mBackgroundReceiverPendingIntent,
                new SubscribeOptions.Builder()
                        .setStrategy(Strategy.BLE_ONLY)
                        .setCallback(new SubscribeCallback() {
                            @Override
                            public void onExpired() {
                                super.onExpired();
                                Log.i(LOG_TAG, "Background Subscribing Expired");
                            }
                        }).build());
    }

    private void backgroundUnsubscribe() {
        Log.i(LOG_TAG, "Background Ubsubscribing");
        Nearby.getMessagesClient(this).unsubscribe(mBackgroundReceiverPendingIntent);
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getBroadcast(this, 0, new Intent(this, NearbyMessagesBackgroundReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
