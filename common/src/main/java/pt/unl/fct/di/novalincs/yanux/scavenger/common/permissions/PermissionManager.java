/*
 * Copyright (c) 2016 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.R;

public class PermissionManager {
    public static final int REQUEST_PERMISSION_GENERIC = 0;
    public static final int REQUEST_PERMISSION_LOCATION = 1;

    private final Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    public static boolean werePermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return grantResults.length > 0;
    }

    public void requestPermission(final String permission) {
        final String rationaleMessage;
        final int requestCode;
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                rationaleMessage = activity.getString(R.string.permission_rationale_location);
                requestCode = REQUEST_PERMISSION_LOCATION;
                break;
            default:
                rationaleMessage = activity.getString(R.string.permission_rationale_generic);
                requestCode = REQUEST_PERMISSION_GENERIC;
                break;
        }
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionRationale(rationaleMessage, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{permission},
                                        requestCode);
                            }
                        }
                );
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        }
    }

    private void showPermissionRationale(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity).setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }
}
