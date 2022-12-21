package devs.mrp.coolyourturkey.comun.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import devs.mrp.coolyourturkey.comun.GenericCache;

public class GenericCacheImpl<T,K> implements GenericCache<T,K> {

    private Map<K,TimeStampedData> cache = Collections.synchronizedMap(TurkeyFactoryProvider.<K,TimeStampedData>getCacheMap(500).getInstance());
    private long millisToExpire;

    public GenericCacheImpl() {
        millisToExpire = 60*1000; // 1 minute
    }

    public GenericCacheImpl(int minutes) {
        millisToExpire = minutes * 60 * 1000;
    }

    @Override
    public T get(K key) {
        TimeStampedData tsd = cache.get(key);
        if (tsd == null || tsd.isExpired()) {
            return null;
        }
        return tsd.data;
    }

    @Override
    public void put(K key, T value) {
        cache.put(key, new TimeStampedData(value));
    }

    private class TimeStampedData {
        LocalDateTime timeStamp;
        T data;
        TimeStampedData(T t) {
            timeStamp = LocalDateTime.now();
            data = t;
        }
        boolean isExpired() {
            return timeStamp.plus(millisToExpire, ChronoUnit.MILLIS).isBefore(LocalDateTime.now());
        }
    }
}
