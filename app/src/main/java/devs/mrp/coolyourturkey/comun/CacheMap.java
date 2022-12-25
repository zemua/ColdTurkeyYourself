package devs.mrp.coolyourturkey.comun;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheMap<K,V> extends LinkedHashMap<K,V> {

    private static final String TAG = "CacheMap";

    private int maxSize = 100;

    public CacheMap() {
        maxSize = 100;
    }

    public CacheMap(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> entry) {
        boolean isRemove = this.size() > maxSize;
        if (isRemove) {
            Log.d(TAG, "Removing oldest entry " + entry.toString() + " of map, call stack trace: " + Thread.currentThread().getStackTrace());
        }
        return isRemove;
    }

}
