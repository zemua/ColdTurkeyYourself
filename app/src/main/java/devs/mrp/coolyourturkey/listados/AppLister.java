package devs.mrp.coolyourturkey.listados;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;

public class AppLister {
    private static final String TAG = "AppLister";
    private final PackageManager pm;
    private List<ApplicationInfo> mPackagesList;
    private List<ApplicationInfo> mPackages;
    private Context mContext;

    public AppLister(Context c) {
        pm = c.getPackageManager();
        mContext = c;
        // obtener lista de aplicaciones instaladas
        mPackages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        removeCurrentApp(); // don't list this app for positive-negative
        mPackages.sort(new MyComp());
        mPackagesList = new ArrayList<>(mPackages); // set baseline for operations
    }

    public List<ApplicationInfo> getList(){
        return mPackages;
    }

    public List<ApplicationInfo> setNonSystemList(){
        mPackages = new ArrayList<>(mPackagesList);
        ListIterator<ApplicationInfo> iterator = mPackages.listIterator();
        while (iterator.hasNext()){
            ApplicationInfo app = iterator.next();
            try {
                if (isSystemPackage(pm.getPackageInfo(app.packageName, 0))) {
                    iterator.remove();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return mPackages;
    }

    public List<ApplicationInfo> setSystemList(){
        mPackages = new ArrayList<>(mPackagesList);
        return mPackages;
    }

    public List<ApplicationInfo> setPositiveList(List<AplicacionListada> positiveApps){
        mPackages = new ArrayList<>(mPackagesList); // baseline
        Map<String, String> map = positiveApps.stream().collect(Collectors.toMap(AplicacionListada::getNombre, AplicacionListada::getLista));
        ListIterator<ApplicationInfo> iterator = mPackages.listIterator();
        while (iterator.hasNext()){
            ApplicationInfo app = iterator.next();
            if (!map.containsKey(app.packageName)){
                iterator.remove();
            }
        }
        return mPackages;
    }

    public AppLister removeCurrentApp(){
        ListIterator<ApplicationInfo> iterator = mPackages.listIterator();
        ApplicationInfo thisApp = mContext.getApplicationInfo();
        while (iterator.hasNext()){
            ApplicationInfo app = iterator.next();
            if (app.packageName.equals(thisApp.packageName)){
                iterator.remove();
                break;
            }
        }
        return this;
    }

    public String getNombre(int i) {
        return mPackages.get(i).packageName;
    }

    public String getSourceDir(int i) {
        return mPackages.get(i).sourceDir;
    }

    public Intent getLaunchIntent(int i) {
        return pm.getLaunchIntentForPackage(mPackages.get(i).packageName);
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    class MyComp implements Comparator<ApplicationInfo>{

        @Override
        public int compare(ApplicationInfo o1, ApplicationInfo o2) {
            return mContext.getPackageManager().getApplicationLabel(o1).toString().compareTo(mContext.getPackageManager().getApplicationLabel(o2).toString());
        }
    }
}
