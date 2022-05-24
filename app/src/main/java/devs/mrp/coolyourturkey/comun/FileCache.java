package devs.mrp.coolyourturkey.comun;

import android.net.Uri;

import java.util.function.Supplier;

public interface FileCache<T> {

    public T read(Uri uri);

}
