package devs.mrp.coolyourturkey.comun;

import java.util.function.Supplier;

public interface GenericCache<T,K> {
    public T get(K key);
    public void put(K key, T value);
}
