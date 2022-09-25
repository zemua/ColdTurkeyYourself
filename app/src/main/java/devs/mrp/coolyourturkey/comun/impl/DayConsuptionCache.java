package devs.mrp.coolyourturkey.comun.impl;

import android.app.Application;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import devs.mrp.coolyourturkey.comun.FileCache;
import devs.mrp.coolyourturkey.comun.FileReader;

public class DayConsuptionCache implements FileCache<Map<Long, FileReader.DayConsumption>> {

    private Application application;
    private static final long cacheTime = 1*60*1000; // one minute
    private static Map<Uri, CacheObject> cache = new HashMap<>();

    public DayConsuptionCache(Application app) {
        this.application = app;
    }

    private class CacheObject {
        CacheObject(long updated, Map<Long, FileReader.DayConsumption> datos) {
            lastUpdated = updated;
            data = datos;
        }
        long lastUpdated;
        Map<Long, FileReader.DayConsumption> data;
    }

    @Override
    public Map<Long, FileReader.DayConsumption> read(Uri uri) {
        long now = System.currentTimeMillis();
        if (!cache.containsKey(uri) || cache.get(uri).lastUpdated+cacheTime<now) {
            cache.put(uri, new CacheObject(now, FileReader.readPastDaysConsumptionConsideringChangeOfDay(application, uri)));
        }
        return cache.get(uri).data;
    }

}
