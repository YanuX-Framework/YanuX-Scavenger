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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.cell;

import android.app.Activity;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;

import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;

/*
 * TODO:
 * Finish and use this on a simple example activity.
 */
public class CellCollector {
    private final Context context;
    private final TelephonyManager telephonyManager;
    private PermissionManager permissionManager;

    public CellCollector(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (context instanceof Activity) {
            permissionManager = new PermissionManager((Activity) context);
        }

    }

    public List<CellInfo> getAllCellInfo() throws SecurityException {
        return telephonyManager.getAllCellInfo();
    }

    public int getNetworkType() {
        return telephonyManager.getNetworkType();
    }

    public String getNetworkOperatorName() {
        return telephonyManager.getNetworkOperatorName();
    }

    public String getNetworkOperator() {
        return telephonyManager.getNetworkOperator();
    }

    public boolean isNetworkRoaming() {
        return telephonyManager.isNetworkRoaming();
    }

    public int getSimState() {
        return telephonyManager.getSimState();
    }

    public String getSimOperator() {
        return telephonyManager.getSimOperator();
    }

    public String getSimOperatorName() {
        return telephonyManager.getSimOperatorName();
    }

    public String getSimCountryIso() {
        return telephonyManager.getSimCountryIso();
    }

}
