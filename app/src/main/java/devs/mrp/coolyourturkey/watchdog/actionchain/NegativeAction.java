package devs.mrp.coolyourturkey.watchdog.actionchain;

import android.util.Log;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class NegativeAction extends AbstractHandler{

    private static final String TAG = NegativeAction.class.getSimpleName();

    @Override
    protected boolean canHandle(int tipo){
        if (tipo == ForegroundAppChecker.NEGATIVO) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(WatchDogData data) {
        data.setEstaNotif(ForegroundAppChecker.NEGATIVO);
        discountTime(data);
        updateNotification(data);
        data.getTimePusher().push(data.getNow(), data.getTiempoAcumulado());
        logTime(data);
        sendNotice(data);
        actOnConditions(data); // TODO decouple together with neutral and positive
    }

    private void discountTime(WatchDogData data) {
        if (data.getUltimoContador() != null) {
            long lproporcionMilisTranscurridos = data.getMilisTranscurridos() * data.getProporcion();
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado() - lproporcionMilisTranscurridos);
        }
    }

    private void updateNotification(WatchDogData data) {
        if (data.getEstaNotif() != data.getUltimanotif() || !data.getUltimoNombre().equals(data.getPackageName()) || Math.abs(data.getTiempoAcumulado() - data.getUltimoAcumulado()) > data.getTimeDifferenceToUpdate() || data.getWasPausado() || data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            data.setNotification(data.getWatchDogHandler().getNotificacionNegativa(data.getPackageName(), data.getTiempoAcumulado() + data.getTiempoImportado(), data.getProporcion()));
            data.setUpdated(true);
        } else {
            data.setUpdated(false);
        }
    }

    private void logTime(WatchDogData data) {
        try {
            data.getTimeLogHandler().insertTimeBadApp(data.getPackageName(), data.getMilisTranscurridos());
        } catch (Exception e) {
            Log.e(TAG, "Error logging time for bad app", e);
        }
    }

    private void sendNotice(WatchDogData data) {
        long milis = (data.getTiempoAcumulado() + data.getTiempoImportado()) / data.getProporcion();
        if (milis < data.getMisPreferencias().getMilisToast()) {
            String mensaje = data.getService().getString(R.string.tiempo_restante_para_bloqueo);
            String time = MilisToTime.getFormated(milis);
            mensaje = mensaje.concat(time);
            data.getConditionToaster().noticeMessage(mensaje);
        }
    }

    private void actOnConditions(WatchDogData data) {
        data.getElementAndGroupFacade().onPreventClosing(data.getPackageName(), isPreventClose -> onPreventClose(data, isPreventClose));
    }

    private void onPreventClose(WatchDogData data, boolean isPreventClose) {
        if (isPreventClose) {
            data.setNeedToBlock(false);
        } else if (data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            data.setNeedToBlock(true);
            block(data);
        } else {
            data.getPackageConditionsChecker().onAllConditionsMet(data.getPackageName(), areMet -> onAllConditionsMet(data, areMet));
        }
    }

    private void onAllConditionsMet(WatchDogData data, boolean isConditionsMet) {
        if (isConditionsMet) {
            onEnoughTime(data);
        } else {
            data.setNeedToBlock(true);
            block(data);
        }
    }

    private void onEnoughTime(WatchDogData data) {
        if (data.getTiempoAcumulado() + data.getTiempoImportado() <= 0) {
            data.setNeedToBlock(true);
            block(data);
        } else {
            data.setNeedToBlock(false);
        }
    }

    private void block(WatchDogData data) {
        data.getScreenBlock().go();
    }

}
