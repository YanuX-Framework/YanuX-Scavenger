package pt.unl.fct.di.novalincs.yanux.scavenger.common.cell;

import android.app.Activity;
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;

import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;

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

    public CellLocation getCellLocation() {
        return telephonyManager.getCellLocation();
    }
}
