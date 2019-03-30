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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class PersistentServiceMainLooperHandler extends Handler {
    public static final int HANDLE_SHOW_TOAST = 0;

    private Context context;

    public PersistentServiceMainLooperHandler(Context context) {
        super(Looper.getMainLooper());
        this.context = context;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case HANDLE_SHOW_TOAST:
                Toast.makeText(context, message.obj.toString(), Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
