package devs.mrp.coolyourturkey.workspace;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

public class TurkeyBroadcaster {

    public static String PN_BROADCAST = "devs.mrp.coolyourturkey.PN_BROADCAST";

    public static int POSITIVA = 0;
    public static int NEGATIVA = 1;
    public static int NEUTRA = 2;

    public static String TIPO = "tipo";
    public static String MILIS = "milis";

    MisPreferencias mp;

    public TurkeyBroadcaster(Context c) {
        mp = new MisPreferencias(c);
    }

    public void enviarActividad(Context c, int tipo, long milis) {
        //if (mp.getBroadcastOn() && tipo != NEUTRA) {
            Intent intent = new Intent();
            intent.setAction(PN_BROADCAST);
            //intent.setPackage(c.getPackageName());
            intent.putExtra(TIPO, tipo);
            intent.putExtra(MILIS, milis);
            c.sendBroadcast(intent);
        //}
    }



}
