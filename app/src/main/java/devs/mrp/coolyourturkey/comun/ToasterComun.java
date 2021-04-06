package devs.mrp.coolyourturkey.comun;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.os.HandlerCompat;

public class ToasterComun {

    public static final int LARGO = Toast.LENGTH_LONG;
    public static final int CORTO = Toast.LENGTH_SHORT;

    public static void toastInMainThread(Context context, String mensaje, int length){
        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, mensaje, length);
                toast.show();
            }
        });
    }

}
