package devs.mrp.coolyourturkey.comun.impl;

import java.util.LinkedHashMap;

import devs.mrp.coolyourturkey.comun.CacheMap;
import devs.mrp.coolyourturkey.comun.GenericCache;
import devs.mrp.coolyourturkey.comun.TurkeyFactory;

public class TurkeyFactoryProvider {
    public static <K,V> TurkeyFactory<LinkedHashMap<K,V>> getCacheMap(int size) {
        return () -> new CacheMap<>(size);
    }

    public static <T,K> TurkeyFactory<GenericCache<T,K>> getGenericCache() {
        return () -> new GenericCacheImpl<>();
    }
}
