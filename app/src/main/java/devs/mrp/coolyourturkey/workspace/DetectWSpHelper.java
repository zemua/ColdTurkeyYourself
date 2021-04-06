package devs.mrp.coolyourturkey.workspace;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

public class DetectWSpHelper {

    private String TAG = "DetectWSpHelper";

    public boolean needToDiscount(Context c, MisPreferencias p){
        return isWorkProfileActive(c) && isNegative(p);
    }

    private boolean isWorkProfileActive(Context context){
        DevicePolicyManager manager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        List<ComponentName> activeAdmins = manager.getActiveAdmins();

        Log.d(TAG, "comprobando activeAdmins");
        if (activeAdmins != null) {
            Log.d(TAG, "Hay 'activeAdmins'");
            for (ComponentName admin : activeAdmins) {
                String packageName = admin.getPackageName();
                if (manager.isProfileOwnerApp(packageName)) {
                    Log.d(TAG, "Work Profile is: " + packageName);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isNegative(MisPreferencias mMisPreferencias){
        return mMisPreferencias.getWorkProfileNegative();
    }
}
