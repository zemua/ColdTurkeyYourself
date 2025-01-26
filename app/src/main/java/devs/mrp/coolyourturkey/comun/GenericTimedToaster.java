package devs.mrp.coolyourturkey.comun;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.os.HandlerCompat;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

public class GenericTimedToaster {

    private Application mContext;
    private Long lastToastTime = 0L;
    private Long mTiempoEntreToasts = 1000L * 60L; // 1 minuto entre avisos
    private MisPreferencias mMisPreferencias;

    public GenericTimedToaster(Application context) {
        mContext = context;
        mMisPreferencias = new MisPreferencias(context);
    }

    public void noticeMessage(String message) {
        Long now = System.currentTimeMillis();
        if (istimeUp(now)) {
            lastToastTime = now;
            toastInMainThread(mContext, message, Toast.LENGTH_LONG);
        }
    }

    public void noticeMessageWithoutFloodProtection(String message) {
        toastInMainThread(mContext, message, Toast.LENGTH_LONG);
    }

    private boolean istimeUp(Long now) {
        if (now - mTiempoEntreToasts > lastToastTime) {
            return true;
        }
        return false;
    }

    private void toastInMainThread(Context context, String mensaje, int length){
        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, mensaje, length);
                toast.show();
            }
        });
    }

    public void setOffsetMinutes(int minutes) {
        mTiempoEntreToasts = minutes * 1000L * 60L;
    }

}
