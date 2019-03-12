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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class MobilePersistentServiceBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_DISABLE_SERVICE = "pt.unl.fct.di.novalincs.yanux.scavenger.NOTIFICATION_CHANNEL.ACTION_DISABLE_SERVICE";

    private static final String LOG_TAG = Constants.LOG_TAG + "_" + MobilePersistentService.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, intent.toString());
        if (ACTION_DISABLE_SERVICE.equals(intent.getAction())) {
            Preferences preferences = new Preferences(context);
            preferences.setIsPersistentServiceAllowed(false);
            context.stopService(new Intent(context, MobilePersistentService.class));
        }
    }
}
