package pt.unl.fct.di.novalincs.yanux.scavenger.common.cell;

import android.app.Activity;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;

//TODO: Actually use and finish implementation
public class CellInfoCollector {
    private final Context context;
    private final TelephonyManager telephonyManager;
    private PermissionManager permissionManager;

    public CellInfoCollector(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (context instanceof Activity) {
            permissionManager = new PermissionManager((Activity) context);
        }
    }

    public List<CellInfo> getAllCellInfo() {
        return telephonyManager.getAllCellInfo();
    }

    public List<NeighboringCellInfo> getNeighboringCellInfo() {
        return telephonyManager.getNeighboringCellInfo();
    }

    public CellLocation getCellLocation() {
        return telephonyManager.getCellLocation();
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
