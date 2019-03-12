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

package pt.unl.fct.di.novalincs.yanux.scavenger.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import org.altbeacon.beacon.BeaconConsumer;

import androidx.core.app.NotificationCompat;
import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.activity.MainActivity;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.services.PersistentService;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class MobilePersistentService extends Service implements BeaconConsumer {
    public static final int NOTIFICATION_ID = 1000;
    public static final String NOTIFICATION_TITLE = "YanuX Scavenger Background Service";
    public static final String NOTIFICATION_CONTENT = "Improving your user experience at the cost of your battery";
    public static final String NOTIFICATION_CHANNEL_ID = "pt.unl.fct.di.novalincs.yanux.scavenger.NOTIFICATION_CHANNEL.SILENT";
    public static final String NOTIFICATION_CHANNEL_NAME = "Background Service";
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + MobilePersistentService.class.getSimpleName();
    // Binder given to clients
    private final IBinder mBinder = new MobilePersistentServiceBinder();
    private PersistentService persistentService;

    public MobilePersistentService() {
        super();
        this.persistentService = new PersistentService(this);
    }

    public static void start(Context context) {
        /*
         * Use a plain old (foreground) service
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, MobilePersistentService.class));
        } else {
            context.startService(new Intent(context, MobilePersistentService.class));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, getNotification());
        persistentService.start();
        if (persistentService.isStarted()) {
            return START_STICKY;
        } else {
            stopForeground(true);
            stopSelf(startId);
            return START_NOT_STICKY;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public PersistentService getPersistentService() {
        return persistentService;
    }

    @Override
    public void onBeaconServiceConnect() {
        persistentService.setBeaconServiceConnected(true);
        persistentService.listenForBleBeacons();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        persistentService.stop();
    }

    private Notification getNotification() {
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, homeIntent, 0);

        Intent disableIntent = new Intent(this, MobilePersistentServiceBroadcastReceiver.class);
        disableIntent.setAction(MobilePersistentServiceBroadcastReceiver.ACTION_DISABLE_SERVICE);
        PendingIntent disablePendingIntent = PendingIntent.getBroadcast(this, 0, disableIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_background_service_notification)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_CONTENT)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(R.drawable.ic_disable_persistent_service, getString(R.string.notication_action_disable_persistent_service), disablePendingIntent)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        return mBuilder.build();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class MobilePersistentServiceBinder extends Binder {
        public MobilePersistentService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MobilePersistentService.this;
        }
    }
}
