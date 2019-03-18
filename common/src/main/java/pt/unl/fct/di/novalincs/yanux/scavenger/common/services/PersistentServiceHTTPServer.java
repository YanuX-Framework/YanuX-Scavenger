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

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class PersistentServiceHTTPServer extends NanoHTTPD {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + PersistentService.class.getSimpleName();

    private final Context context;
    private final Preferences preferences;

    public PersistentServiceHTTPServer(Context context, int port) {
        super(port);
        this.context = context;
        this.preferences = new Preferences(context);
    }

    //TODO: Return the device UUID when asked!
    @Override
    public Response serve(IHTTPSession session) {
        Log.d(LOG_TAG, "Request: " + session.getUri());
        Response res;
        try {
            switch (session.getUri()) {
                case "/deviceInfo":
                    JSONObject response = new JSONObject();
                    response.put("deviceUuid", preferences.getDeviceUuid());
                    res = newFixedLengthResponse(Response.Status.OK, "application/json", response.toString());
                    break;
                default:
                    res = newChunkedResponse(Response.Status.OK, "text/html", this.context.getAssets().open("index.html"));
                    break;
            }
            //TODO: Refine the CORS policy!
            res.addHeader("Access-Control-Allow-Origin", "*");
            return res;
        } catch (IOException | JSONException e) {
            Log.e(LOG_TAG, e.toString());
            return newFixedLengthResponse("Error: " + e.toString());
        }
    }
}