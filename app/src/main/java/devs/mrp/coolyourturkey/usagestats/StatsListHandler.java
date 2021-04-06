package devs.mrp.coolyourturkey.usagestats;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class StatsListHandler {

    private Context mContext;
    private Map<String, ApplicationInfo> mInstalledApps = new HashMap<>();
    private StatsHandler mHandler;
    ArrayList<UsageStats> mUso;
    Map<String, UsageStats> mUsoMap;

    public StatsListHandler(Context context) {
        mContext = context;
        mHandler = new StatsHandler(context);
        List<ApplicationInfo> installedApps = context.getPackageManager().getInstalledApplications(0);
        installedApps.forEach((app) -> {
            mInstalledApps.put(app.packageName, app);
        });

        mUso = mHandler.getDetailUsageStats();

        mUsoMap = new HashMap<>();
        mUso.forEach((app) -> {
            mUsoMap.put(app.getPackageName(), app);
        });
    }

    public List<AplicacionListada> quitarDesinstaladas(List<AplicacionListada> appsEnDb) {
        List<AplicacionListada> apps = new ArrayList<>(appsEnDb);
        ListIterator<AplicacionListada> iterator = apps.listIterator();
        while (iterator.hasNext()) {
            AplicacionListada app = iterator.next();
            if (!mInstalledApps.containsKey(app.getNombre())) {
                iterator.remove();
            }
        }
        return apps;
    }

    public List<AplicacionListada> dameTodasLasNeutras(List<AplicacionListada> appsNoNeutras) {
        Map<String, ApplicationInfo> instaladas = new HashMap(mInstalledApps);

        appsNoNeutras.forEach((app) -> {
            if (instaladas.containsKey(app.getNombre())) {
                instaladas.remove(app.getNombre());
            }
        });

        List<AplicacionListada> neutras = new ArrayList<>();
        instaladas.forEach((paquete, apk) -> {
            neutras.add(new AplicacionListada(paquete, AplicacionListada.getNEUTRAL()));
        });

        return neutras;
    }

    public List<AplicacionListada> quitaSinTiempo(List<AplicacionListada> apps) {
        ListIterator<AplicacionListada> iterator = apps.listIterator();
        while (iterator.hasNext()) {
            AplicacionListada app = iterator.next();
            String nombre = app.getNombre();
            UsageStats lus = mUsoMap.get(nombre);
            if (lus == null || lus.getTotalTimeInForeground() < 100) {
                iterator.remove();
            }
        }

        return apps;
    }

    public List<AplicacionListada> ordenaPorTiempo(List<AplicacionListada> apps, Comparator<AplicacionListada> comparator){
        apps.sort(comparator);
        return apps;
    }

    public Comparator<AplicacionListada> getTimeComparator(){
        Comparator<AplicacionListada> c = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                AplicacionListada al1 = (AplicacionListada) o1;
                AplicacionListada al2 = (AplicacionListada) o2;

                long t1 = mUsoMap.get(al1.getNombre()).getTotalTimeInForeground();
                long t2 = mUsoMap.get(al2.getNombre()).getTotalTimeInForeground();

                if (t1 > t2){
                    return -1;
                } else if (t1 < t2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        return c;
    }


}
