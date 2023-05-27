package devs.mrp.coolyourturkey.configuracion;

import android.content.Context;
import android.icu.util.Calendar;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.comun.ToasterComun;

public class ToqueDeQuedaHandler {

    private static long lastAviso = 0L;
    private static long tiempoEntreAvisos = 1000L * 60L; // 1 minuto
    private static final String ID_NOTIFICATION_CHANNEL = ToqueDeQuedaHandler.class.getSimpleName();
    private static final int ID_NOTIFICATION = 273945;

    private Context mContext;
    private MisPreferencias mMisPreferencias;
    private Notificador notificador;

    public ToqueDeQuedaHandler(Context context, MisPreferencias misPreferencias, Notificador notificador) {
        this.mContext = context;
        this.mMisPreferencias = misPreferencias;
        this.notificador = notificador;
    }

    private boolean getCase(long dormir, long despertar, long ahora){
        if (mMisPreferencias.getActivaToqueDeQuedaSiNo()) {
            if (dormir > despertar && ahora > dormir) {
                return true;
            } else if (dormir > despertar && ahora < despertar) {
                return true;
            } else if (dormir < despertar && ahora > dormir && ahora < despertar) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private long getTiempoDormir(){
        return mMisPreferencias.getMilisInicioToqueDeQueda();
    }

    private long getTiempoDespertar(){
        return mMisPreferencias.getMilisFinalToqueDeQueda();
    }

    private long milisToMilis(long milis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milis);
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);

        long nowToMilis = MilisToTime.getMilisDeHoras(h) + MilisToTime.getMilisDeMinutos(m);
        return nowToMilis;
    }

    public boolean isToqueDeQueda(long milis) {
        if (!mMisPreferencias.getActivaToqueDeQuedaSiNo()){
            return false;
        }

        return getCase(getTiempoDormir(), getTiempoDespertar(), milis);
    }

    public boolean isToqueDeQueda(){
        return isToqueDeQueda(milisToMilis(System.currentTimeMillis()));
    }


    private boolean isAvisoToqueDeQueda(long milis) {
        if (!mMisPreferencias.getAvisoToqueDeQuedaSiNo() || !mMisPreferencias.getActivaToqueDeQuedaSiNo()){
            return false;
        }

        long dormir = getTiempoDormir();
        long tiempoAviso = mMisPreferencias.getMinutosAvisoToqueDeQueda() * 60L * 1000L;

        if (milis >= (dormir-tiempoAviso) && milis < dormir){
            return true;
        }

        return false;
    }

    private boolean isAvisoToqueDeQueda(){
        return isAvisoToqueDeQueda(milisToMilis(System.currentTimeMillis()));
    }

    private boolean timeOut(long milis){
        if (milis - tiempoEntreAvisos > lastAviso){
            return true;
        } else {
            return false;
        }
    }

    private void toastPreToque(int minutos){
        ToasterComun.toastInMainThread(mContext,
                String.valueOf(minutos).concat(" ").concat(mContext.getString(R.string.minutos_para_toque_de_queda)),
                ToasterComun.CORTO);
    }

    private void toastEnToque(){
        ToasterComun.toastInMainThread(mContext,
                mContext.getString(R.string.toque_de_queda),
                ToasterComun.LARGO);
    }

    private void notifyPreToque(int minutos) {
        notificador.createNotificationChannel(R.string.notificaciones_toque_de_queda,
                R.string.envia_avisos_toque_de_queda,
                ToqueDeQuedaHandler.ID_NOTIFICATION_CHANNEL);
        notificador.createNotification(R.drawable.police_badge,
                mContext.getString(R.string.notificaciones_toque_de_queda),
                String.valueOf(minutos).concat(" ").concat(mContext.getString(R.string.minutos_para_toque_de_queda)),
                ToqueDeQuedaHandler.ID_NOTIFICATION_CHANNEL,
                ToqueDeQuedaHandler.ID_NOTIFICATION);
    }

    private void notifyEnToque() {
        notificador.createNotificationChannel(R.string.notificaciones_toque_de_queda,
                R.string.envia_avisos_toque_de_queda,
                ToqueDeQuedaHandler.ID_NOTIFICATION_CHANNEL);
        notificador.createNotification(R.drawable.police_badge,
                mContext.getString(R.string.notificaciones_toque_de_queda),
                mContext.getString(R.string.toque_de_queda),
                ToqueDeQuedaHandler.ID_NOTIFICATION_CHANNEL,
                ToqueDeQuedaHandler.ID_NOTIFICATION);
    }

    public void avisar(long milisNow) {
        if (!mMisPreferencias.getAvisoToqueDeQuedaSiNo() || !mMisPreferencias.getActivaToqueDeQuedaSiNo()){
            return;
        }
        long misMilis = milisToMilis(milisNow);
        if (isToqueDeQueda(misMilis) && timeOut(milisNow)){
            notifyEnToque();
            lastAviso = System.currentTimeMillis();
        } else if (isAvisoToqueDeQueda(misMilis) && timeOut(milisNow)){
            notifyPreToque(MilisToTime.getMinutos(getTiempoDormir()-misMilis).intValue());
            lastAviso = System.currentTimeMillis();
        }
    }

    public void avisar(){
        avisar(System.currentTimeMillis());
    }
}
