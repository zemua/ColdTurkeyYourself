package devs.mrp.coolyourturkey.comun;

import android.net.Uri;

public interface FileTimeGetter {

    public long fromFileDaysAgo(int daysAgo, Uri uri);
    public long fromFileLastDays(int lastDays, Uri uri);

}
