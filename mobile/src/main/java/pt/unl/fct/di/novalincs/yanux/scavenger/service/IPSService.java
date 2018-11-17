package pt.unl.fct.di.novalincs.yanux.scavenger.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;

import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons.BeaconCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;

public class IPSService extends JobIntentService implements BeaconConsumer {
    private static final String TAG = "YXS_IPSService";
    private static final int JOB_ID = 1000;
    private boolean isNew = true;
    private Preferences preferences;
    private BeaconCollector beaconCollector;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, IPSService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "IPSService: onHandleWork BEGIN");
        if(isNew) {
            isNew = false;
            preferences = new Preferences(this);
            /** UUID Generation **/
            String deviceUuid = preferences.getDeviceUuid();
            if(deviceUuid == null) {
                deviceUuid = UUID.randomUUID().toString();
                preferences.setDeviceUuid(deviceUuid);
                Log.d(TAG, "New Device UUID: "+deviceUuid);
            } else {
                Log.d(TAG, "Current Device UUID: "+deviceUuid);
            }

            Handler h = new Handler(getApplicationContext().getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    beaconCollector = new BeaconCollector(IPSService.this, new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            switch (intent.getAction()) {
                                case BeaconCollector.ACTION_BEACON_RANGE_BEACONS:
                                    List<Beacon> beaconsArrayList = intent.getParcelableArrayListExtra(BeaconCollector.EXTRA_BEACONS);
                                    for(Beacon b : beaconsArrayList) {
                                        Log.d(TAG, "Beacon: "+b.toString());
                                    }
                                    Log.d(TAG, "Ranging Elapsed Time: "+beaconCollector.getRangingElapsedTime()+"\" ms\"");
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    Log.d(TAG, "IPSService: onHandleWork END");
                }
            });
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconCollector.startRanging();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconCollector.unbind();
    }
}
