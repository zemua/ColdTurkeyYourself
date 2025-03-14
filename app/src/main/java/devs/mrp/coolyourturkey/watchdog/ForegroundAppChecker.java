package devs.mrp.coolyourturkey.watchdog;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import devs.mrp.coolyourturkey.comun.PermisosChecker;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.usagestats.ForegroundAppSpec;

public class ForegroundAppChecker {

    private static final String TAG = "FOREGROUND APP CHECKER CLASS";

    List<String> appsForeground;
    Map<String, AplicacionListada> mAppsBuenas;
    Map<String, AplicacionListada> mAppsMalas;

    private String mPackageName;
    private String mClassName;
    private Context mContext;
    private Long lastQueryTime;
    private String lastPackage;
    private String lastPackageActivity;
    private Long lastPackageTime;
    private UsageStatsManager mUsageStatsManager;
    private UsageEvents.Event event;

    public static final int POSITIVO = 0;
    public static final int NEGATIVO = 1;
    public static final int NEUTRO = 3;
    public static final int NULL = -1;
    private int tipoAppForeground = NEUTRO;

    ForegroundAppChecker(Context context, Map<String, AplicacionListada> appsBuenas, Map<String, AplicacionListada> appsMalas) {
        appsForeground = new ArrayList<>();
        mAppsBuenas = appsBuenas;
        mAppsMalas = appsMalas;
        mContext = context;
        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        forget();
    }

    private final void forget(){
        lastQueryTime = 0L;
        lastPackage = null;
        lastPackageActivity = null;
        lastPackageTime = 0L;
        event = new UsageEvents.Event();
    }

    // solamente pre-lollipop
    @Deprecated
    public int getForegroundType(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return -1;
        } else {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            try {
                ComponentName topTask = activityManager.getRunningTasks(1).get(0).topActivity;
                mPackageName = topTask.getPackageName();
                mClassName = topTask.getClassName();
            } catch (Exception e) {
                mPackageName = null;
                mClassName = null;
            }

            if (mPackageName == null) {
                tipoAppForeground = NEUTRO;
            } else if (mAppsBuenas.containsKey(mPackageName)) {
                tipoAppForeground = POSITIVO;
            } else if (mAppsMalas.containsKey(mPackageName)) {
                tipoAppForeground = NEGATIVO;
            } else {
                tipoAppForeground = NEUTRO;
            }

            return tipoAppForeground;
        }
    }

    // post-lollipop forma 1
    // esta solamente detecta qué app tuvo cambio de activity en los ultimos x segundos
    // no vale para lo que queremos
    @Deprecated
    public String getTopPackageNameByTree(Context context, long sleeptime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - sleeptime, time);
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                mPackageName = null;
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    mPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
                return mPackageName;
            }
            return null;
        }
        return null;
    }

    // post-lollipop forma 2
    // esta detecta la app que tuvo cambio de actividad en los ultimos x segundos
    // no vale para lo que queremos
    @Deprecated
    public String getTopPackageNameByLoop(Context context, long sleeptime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPackageName = null;
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            // get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 1000 * 10, currentTime);
            // search for app with most recent last used time
            if (stats != null) {
                long lastUsedAppTime = 0;
                for (UsageStats usageStats : stats) {
                    if (usageStats.getLastTimeUsed() > lastUsedAppTime) {
                        mPackageName = usageStats.getPackageName();
                        lastUsedAppTime = usageStats.getLastTimeUsed();
                    }
                }
                return mPackageName;
            }
            return null;
        }
        return null;
    }

    @Deprecated
    public int getTopAppTypeByLoop(Context context, long sleeptime){
        String lTopApp = getTopPackageNameByLoop(context, sleeptime);
        return packageNameToCodeType(mPackageName);
    }

    @Deprecated
    public int getTopAppTypeByTree(Context context, long sleeptime){
        String lTopApp = getTopPackageNameByTree(context, sleeptime);
        return packageNameToCodeType(mPackageName);
    }

    public ForegroundAppSpec getForegroundApp(ForegroundAppSpec result, Long queryInterval) throws SecurityException{
        if (!PermisosChecker.checkPermisoEstadisticas(mContext)){
            //throw new SecurityException();
            Log.d(TAG, "sin permisos para ver el tiempo de uso");
        }
        Long now = System.currentTimeMillis();
        if (lastQueryTime > now || queryInterval >= 1000*60*60*24 /* 1 día */){
            // si el tiempo fue para atrás reseteamos
            forget();
        }

        Long queryStartTime;
        if(lastQueryTime == 0L){
            // datos de los últimos 7 dias
            queryStartTime = now - 1000*60*60*24*7;
        } else {
            // datos del query desde el último query
            // nota: cuando la duración es muy pequeña, Android no devuelve datos
            //       se consulta 1 segundo más de lo que necesitamos
            //       el cual parece devolver todos los datos
            // actualización: con 1 segundo algunos eventos se pierden
            //         parece que siempre funciona con 1.5 segundos
            queryStartTime = lastQueryTime - Math.max(queryInterval, 1500);
        }

        UsageEvents levents = mUsageStatsManager.queryEvents(queryStartTime, now);
        while (levents.hasNextEvent()) {
            levents.getNextEvent(event);

            if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED /* MOVE_TO_FOREGROUND deprecated */) {
                if (event.getTimeStamp() >= lastPackageTime) {
                    lastPackageTime = event.getTimeStamp();
                    lastPackage = event.getPackageName();
                    lastPackageActivity = event.getClassName();
                }
            }
        }

        result.packageName = lastPackage;
        result.activityName = lastPackageActivity;
        result.appType = packageNameToCodeType(lastPackage);

        Log.d(TAG, "Retrieved event info -- package name: " + result.packageName + " / activity name: " + result.activityName + " / app type: " + result.appType);

        lastQueryTime = now;

        return result;
    }

    private int packageNameToCodeType(String nombre){
        if (nombre == null || nombre == ""){
            return NULL;
        } else if (mAppsMalas.containsKey(nombre)){
            return NEGATIVO;
        } else if (mAppsBuenas.containsKey(nombre)){
            return POSITIVO;
        } else {
            return NEUTRO;
        }
    }

    public String getStoredPackageName(){
        return mPackageName;
    }

    @Deprecated
    List<String> getAppsForeground() {
        return appsForeground;
    }

    public void actualizaBuenas(Map<String, AplicacionListada> buenas) {
        mAppsBuenas = buenas;
    }

    public void actualizaMalas(Map<String, AplicacionListada> malas) {
        mAppsMalas = malas;
    }
}
