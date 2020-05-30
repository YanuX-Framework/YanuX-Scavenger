/*
 * Copyright (c) 2020 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;

public class PermissionManager {
    public static final int REQUEST_MULTIPLE_PERMISSIONS = 0;
    public static final int REQUEST_PERMISSION_GENERIC = 1;
    public static final int REQUEST_PERMISSION_LOCATION = 2;


    private final Activity context;
    private final Preferences preferences;

    public PermissionManager(Activity context) {
        this.context = context;
        this.preferences = new Preferences(context);
    }

    public static boolean werePermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return grantResults.length > 0;
    }

    public void requestPermissions(final String[] permissions, final String[] rationaleMessages, final int requestCode) {
        StringBuilder rationaleMessage = new StringBuilder();
        for (String msg : rationaleMessages) {
            rationaleMessage.append(msg + "\n");
        }
        final List<String> requiredPermissions = new ArrayList<>(permissions.length);
        boolean showRationale = false;
        for (final String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                if (preferences.shouldShowRequestPermissionRationale(permission)
                        || ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                    preferences.setShouldShowRequestRationale(permission, false);
                    showRationale = true;
                }
                requiredPermissions.add(permission);
            }
        }
        if (!requiredPermissions.isEmpty()) {
            if (showRationale) {
                showPermissionRationale(rationaleMessage.toString(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(context, requiredPermissions.toArray(new String[0]), requestCode);
                    }
                });
            } else {
                ActivityCompat.requestPermissions(context, requiredPermissions.toArray(new String[0]), requestCode);
            }
        }
    }

    public void requestPermissions(final String[] permissions, final String[] rationaleMessages) {
        requestPermissions(permissions, rationaleMessages, REQUEST_MULTIPLE_PERMISSIONS);
    }

    public void requestPermissions(final String[] permissions) {
        requestPermissions(permissions,
                new String[]{context.getString(R.string.multiple_permissions_rationale)},
                REQUEST_MULTIPLE_PERMISSIONS);
    }

    public void requestPermission(final String permission) {
        final String rationaleMessage;
        final int requestCode;
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                rationaleMessage = context.getString(R.string.permission_rationale_location);
                requestCode = REQUEST_PERMISSION_LOCATION;
                break;
            default:
                rationaleMessage = context.getString(R.string.permission_rationale_generic);
                requestCode = REQUEST_PERMISSION_GENERIC;
                break;
        }
        requestPermissions(new String[]{permission}, new String[]{rationaleMessage}, requestCode);
    }

    public boolean hasPermissions(final String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPermission(final String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissionRationale(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context).setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }
}
