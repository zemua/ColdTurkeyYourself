package devs.mrp.coolyourturkey.comun.impl;

import android.app.Application;
import android.net.Uri;

import java.util.Map;

import devs.mrp.coolyourturkey.comun.FileCache;
import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.comun.FileTimeGetter;

public class FileTimeGetterImpl implements FileTimeGetter {

    private FileCache<Map<Long, FileReader.DayConsumption>> cache;

    public FileTimeGetterImpl(Application app) {
        cache = new DayConsuptionCache(app);
    }

    @Override
    public long fromFileDaysAgo(long daysAgo, Uri uri) {
        Map<Long, FileReader.DayConsumption> map = cache.read(uri);
        return map.containsKey(daysAgo) ? map.get(daysAgo).getConsumption() : 0;
    }

    @Override
    public long fromFileLastDays(long lastDays, Uri uri) {
        long result = 0;
        for (int i = 0; i<lastDays+1; i++) {
            result += fromFileDaysAgo(i, uri);
        }
        return result;
    }
}
