package pt.unl.fct.di.novalincs.yanux.scavenger.activity.nearby;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class NearbyBackgroundReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = Constants.LOG_TAG + "_NEARBY_BCKGRND_RCVR";

    @Override
    public void onReceive(Context context, Intent intent) {
        Nearby.getMessagesClient(context).handleIntent(intent, new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.i(LOG_TAG, "Found Message (PendingIntent): " + message);
            }

            @Override
            public void onLost(Message message) {
                Log.i(LOG_TAG, "Lost Message (PendingIntent): " + message);
            }
        });

    }
}
