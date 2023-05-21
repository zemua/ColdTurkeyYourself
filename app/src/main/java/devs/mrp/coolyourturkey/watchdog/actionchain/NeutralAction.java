package devs.mrp.coolyourturkey.watchdog.actionchain;

import android.util.Log;

import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class NeutralAction extends AbstractHandler{

    private static final String TAG = NeutralAction.class.getSimpleName();

    @Override
    protected boolean canHandle(int tipo) {
        if (tipo == ForegroundAppChecker.NEUTRO) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(WatchDogData data) {
        handleNotification(data);
        if (isLockdownDecrease(data)) {
            decreasePoints(data);
        } else {
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado());
        }
        logTimeUsage(data);
        data.setNeedToBlock(false);
    }

    private void handleNotification(WatchDogData data) {
        data.setEstaNotif(ForegroundAppChecker.NEUTRO);
        if (shouldUpdateNotification(data)) {
            data.setNotification(data.getWatchDogHandler().getNotificacionNeutra(data.getPackageName(), data.getTiempoAcumulado() + data.getTiempoImportado(), data.getProporcion()));
            data.setUpdated(true);
        } else {
            data.setUpdated(false);
        }
    }

    private boolean shouldUpdateNotification(WatchDogData data) {
        return data.getEstaNotif() != data.getUltimanotif() ||
                !data.getUltimoNombre().equals(data.getPackageName()) ||
                Math.abs(data.getTiempoAcumulado() - data.getUltimoAcumulado()) > data.getTimeDifferenceToUpdate() ||
                data.getWasPausado() ||
                data.getToqueDeQuedaHandler().isToqueDeQueda();
    }

    private boolean isLockdownDecrease(WatchDogData data) {
        return data.getToqueDeQuedaHandler().isToqueDeQueda() &&
                data.getMisPreferencias().getBoolean(PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE,
                        PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE.getDefaultState());
    }

    private void decreasePoints(WatchDogData data) {
        if (data.getUltimoContador() != null) {
            long lproporcionMilisTranscurridos = Math.abs(data.getMilisTranscurridos() * data.getProporcion());
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado() - lproporcionMilisTranscurridos);
            data.getTimePusher().push(data.getNow(), data.getTiempoAcumulado());
        }
    }

    private void logTimeUsage(WatchDogData data) {
        try {
            data.getTimeLogHandler().insertTimeNeutralApp(data.getPackageName(), data.getMilisTranscurridos());
        } catch (Exception e) {
            Log.e(TAG, "Error logging time for neutral app", e);
        }
    }
}
