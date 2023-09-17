package devs.mrp.coolyourturkey.comun;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.PermisosDialogFragment;

public class PermisosChecker {

    public static String DIALOGO_PERMISOS = "Dialogo_Permisos";

    public static boolean checkPermisoEstadisticas(Context contexto) {
        try {
            PackageManager packageManager = contexto.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(contexto.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) contexto.getSystemService(Context.APP_OPS_SERVICE);
            int mode;
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            } else{
                mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean checkPermisoAlertas(Context contexto){
        if(!Settings.canDrawOverlays(contexto)){
            return false;
        }
        return true;
    }

    public static void muestraDialogoPermisos(FragmentManager fm, Fragment fr, int tipoPermiso, String titulo, String mensaje) {
        PermisosDialogFragment dialogo = new PermisosDialogFragment(titulo, mensaje);
        dialogo.setTargetFragment(fr, tipoPermiso);
        dialogo.show(fm, DIALOGO_PERMISOS);
    }

    public static void requestPermisoEstadisticas(Context contexto) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        contexto.startActivity(intent);
    }

    public static void requestPermisoAlertas(Context contexto){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        contexto.startActivity(intent);
    }

    public static void requestPermisoNotificaciones(Context context) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getApplicationInfo().packageName);
            context.startActivity(intent);
        }
    }
}
