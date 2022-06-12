package devs.mrp.coolyourturkey.comun;

import android.net.Uri;

public interface FileTimeGetter {

    public long fromFileDaysAgo(long daysAgo, Uri uri);
    public long fromFileLastDays(long lastDays, Uri uri);

}
