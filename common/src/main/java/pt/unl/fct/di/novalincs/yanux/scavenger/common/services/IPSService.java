package pt.unl.fct.di.novalincs.yanux.scavenger.common.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;

import java.util.List;
import java.util.UUID;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class IPSService {
    private static final String LOG_TAG = Constants.LOG_TAG+"_"+IPSService.class.getSimpleName();
    private Preferences preferences;
    private BeaconCollector beaconCollector;
    private BeaconConsumer beaconConsumer;
    private boolean beaconServiceConnected;

    public IPSService(BeaconConsumer beaconConsumer) {
        this.beaconConsumer = beaconConsumer;
    }

    public void start() {
        Log.d(LOG_TAG, "MobileService: Start");
        preferences = new Preferences(beaconConsumer.getApplicationContext());
        /** UUID Generation **/
        String deviceUuid = preferences.getDeviceUuid();
        if (deviceUuid == null) {
            deviceUuid = UUID.randomUUID().toString();
            preferences.setDeviceUuid(deviceUuid);
            Log.d(LOG_TAG, "New Device UUID: " + deviceUuid);
        } else {
            Log.d(LOG_TAG, "Current Device UUID: " + deviceUuid);
        }
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
}
