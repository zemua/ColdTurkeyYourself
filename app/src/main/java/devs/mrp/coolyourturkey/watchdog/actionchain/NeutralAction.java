package devs.mrp.coolyourturkey.watchdog.actionchain;

import android.util.Log;

import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class NeutralAction extends AbstractHandler{

    private static final String TAG = NeutralAction.class.getSimpleName();

    public NeutralAction(PointsUpdater pointsUpdater) {
        super(pointsUpdater);
    }

    @Override
    protected boolean canHandle(int tipo) {
        if (tipo == ForegroundAppChecker.NEUTRO) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(WatchDogData data) {
        if (isLockdownDecrease(data)) {
            pointsUpdater.decreasePoints(data);
        } else {
            pointsUpdater.keepPoints(data);
        }
        logTimeUsage(data);
        data.setNeedToBlock(false);
        handleNotification(data); // this at the end as it needs the processed data
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

    private void logTimeUsage(WatchDogData data) {
        try {
            data.getTimeLogHandler().insertTimeNeutralApp(data.getPackageName(), data.getMilisTranscurridos());
        } catch (Exception e) {
            Log.e(TAG, "Error logging time for neutral app", e);
        }
    }
}
