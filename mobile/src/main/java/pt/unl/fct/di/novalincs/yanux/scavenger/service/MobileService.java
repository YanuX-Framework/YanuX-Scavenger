package pt.unl.fct.di.novalincs.yanux.scavenger.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import org.altbeacon.beacon.BeaconConsumer;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.services.IPSService;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class MobileService extends Service implements BeaconConsumer {
    private static final String LOG_TAG = Constants.LOG_TAG+"_"+MobileService.class.getSimpleName();
    private IPSService ipsService;

    public static final int NOTIFICATION_ID = 1000;
    public static final String NOTIFICATION_TITLE = "YanuX Scavenger Background Service";
    public static final String NOTIFICATION_CONTENT = "Improving your user experience at the cost of your battery";
    public static final String NOTIFICATION_CHANNEL_ID = "pt.unl.fct.di.novalincs.yanux.scavenger.NOTIFICATION_CHANNEL.SILENT";
    public static final String NOTIFICATION_CHANNEL_NAME = "Background Service";

    public MobileService() {
        super();
        ipsService = new IPSService(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        ipsService.setBeaconServiceConnected(true);
        ipsService.listenForBleBeacons();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ipsService.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ipsService.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, getNotification());
        return START_STICKY;
    }

    private Notification getNotification()  {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_background_service_notification)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_CONTENT)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);

            notificationManager.createNotificationChannel(channel);
        }
        return mBuilder.build();
    }
}
