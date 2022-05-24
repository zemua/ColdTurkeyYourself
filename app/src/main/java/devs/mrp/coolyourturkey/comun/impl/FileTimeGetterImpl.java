package devs.mrp.coolyourturkey.comun.impl;

import android.app.Application;
import android.net.Uri;

import java.util.Map;

import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.comun.FileTimeGetter;

public class FileTimeGetterImpl implements FileTimeGetter {

    private Map<Long, FileReader.DayConsumption> map;

    public FileTimeGetterImpl(Application app, String uri) {
        this(app, Uri.parse(uri));
    }

    public FileTimeGetterImpl(Application app, Uri uri) {
        map = new DayConsuptionCache(app).read(uri);
    }

    @Override
    public long fromFileDaysAgo(int daysAgo) {
        return map.containsKey(daysAgo) ? map.get(daysAgo).getConsumption() : 0;
    }

    @Override
    public long fromFileLastDays(int lastDays) {
        long result = 0;
        for (int i = 0; i<lastDays; i++) {
            result += fromFileDaysAgo(i);
        }
        return result;
    }
}
