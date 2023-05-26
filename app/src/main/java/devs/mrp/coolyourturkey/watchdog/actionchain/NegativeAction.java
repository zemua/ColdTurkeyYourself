package devs.mrp.coolyourturkey.watchdog.actionchain;

import android.util.Log;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class NegativeAction extends AbstractHandler{

    private static final String TAG = NegativeAction.class.getSimpleName();

    public NegativeAction(PointsUpdater pointsUpdater) {
        super(pointsUpdater);
    }

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
        pointsUpdater.decreasePoints(data);
        logTime(data);
        sendNotice(data);
        actOnConditions(data);
        updateNotification(data); // this at the end as it needs the processed data
    }

    private void updateNotification(WatchDogData data) {
        data.setNotification(data.getWatchDogHandler().getNotificacionNegativa(data.getPackageName(), data.getTiempoAcumulado() + data.getTiempoImportado(), data.getProporcion()));
        data.setUpdated(true);
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
        } else if (shallBlockOnToqueDeQueda(data)) {
            data.setNeedToBlock(true);
            block(data);
        } else {
            data.getPackageConditionsChecker().onAllConditionsMet(data.getPackageName(), areMet -> onAllConditionsMet(data, areMet));
        }
    }

    private boolean shallBlockOnToqueDeQueda(WatchDogData data) {
        return data.getToqueDeQuedaHandler().isToqueDeQueda() &&
                data.getMisPreferencias().getBoolean(PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK, true);
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
