package devs.mrp.coolyourturkey.configuracion;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ConfiguracionPermisos {

    public void checkPermisosReadWrite(Context context, Intent intent, Uri uri) {
        final int takeFlags = intent.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // Check for the freshest data.
        context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
    }

}
