package devs.mrp.coolyourturkey.workspace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.concurrent.atomic.AtomicLong;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

public class TurkeyBroadreader extends BroadcastReceiver {

    private static final String TAG = "Time Turkey - TurkeyBroadreader";
    private static AtomicLong tiempoMilis = new AtomicLong(0L);

    @Override
    public void onReceive(Context context, Intent intent) {
        MisPreferencias mp = new MisPreferencias(context);
        //if (!mp.getBroadcastOn()){
            Log.d(TAG, "broadcast recibido");
            Integer tipo = intent.getIntExtra(TurkeyBroadcaster.TIPO, TurkeyBroadcaster.NEUTRA);
            Long milis = intent.getLongExtra(TurkeyBroadcaster.MILIS, 0L);
            if (tipo == TurkeyBroadcaster.POSITIVA){
                tiempoMilis.addAndGet(milis);
            } else if (tipo == TurkeyBroadcaster.NEGATIVA){
                tiempoMilis.addAndGet(-milis);
            }
        //}
    }

    public static long getMilis(){
        return tiempoMilis.longValue();
    }

    public static long getAndReset(){
        return tiempoMilis.getAndSet(0L);
    }

    public static boolean isNegativa() {
        return tiempoMilis.longValue()<0;
    }
}
