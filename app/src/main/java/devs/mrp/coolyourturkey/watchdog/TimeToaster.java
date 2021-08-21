package devs.mrp.coolyourturkey.watchdog;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.os.HandlerCompat;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

public class TimeToaster {

    private final int POSITIVO = ForegroundAppChecker.POSITIVO;
    private final int NEGATIVO = ForegroundAppChecker.NEGATIVO;
    private final int NEUTRO = ForegroundAppChecker.NEUTRO;
    private final int NULO = ForegroundAppChecker.NULL;

    private final int LARGO = Toast.LENGTH_LONG;
    private final int CORTO = Toast.LENGTH_SHORT;
    private Application mContext;
    private static Long lastToasttime = 0L;
    private static final Long TIEMPO_ENTRE_TOAST = 1000L * 60L; // 1 minuto entre avisos
    //private static final Long BARRERA_TIEMPO = 1000L * 60L * 10L; // 10 minutos left para saltar alarma
    private MisPreferencias mMisPreferencias;


    public TimeToaster(Application context) {
        mContext = context;
        mMisPreferencias = new MisPreferencias(context);
    }

    public void noticeTimeLeft(Long milis) {
        if (milis < mMisPreferencias.getMilisToast()) {
            Long now = System.currentTimeMillis();
            if (istimeUp(now)) {
                lastToasttime = now;
                String mensaje = mContext.getString(R.string.tiempo_restante_para_bloqueo);
                String time = MilisToTime.getFormated(milis);
                mensaje = mensaje.concat(time);
                toastInMainThread(mContext, mensaje, LARGO);
            }
        }
    }

    public void noticeChanged(int tipo){
        switch (tipo){
            case POSITIVO:
                toastInMainThread(mContext, mContext.getString(R.string.app_positiva_detectada), CORTO);
                break;
            case NEGATIVO:
                toastInMainThread(mContext, mContext.getString(R.string.app_negativa_detectada), CORTO);
                break;
            case NEUTRO:
                toastInMainThread(mContext, mContext.getString(R.string.app_neutral_detectada), CORTO);
                break;
        }
    }

    private boolean istimeUp(Long now) {
        if (now - TIEMPO_ENTRE_TOAST > lastToasttime) {
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
}
