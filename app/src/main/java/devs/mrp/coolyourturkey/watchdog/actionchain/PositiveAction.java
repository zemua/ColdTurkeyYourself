package devs.mrp.coolyourturkey.watchdog.actionchain;

import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class PositiveAction extends AbstractHandler{

    private static final String TAG = PositiveAction.class.getSimpleName();

    public PositiveAction(PointsUpdater pointsUpdater) {
        super(pointsUpdater);
    }

    @Override
    protected boolean canHandle(int tipo) {
        if (tipo == ForegroundAppChecker.POSITIVO) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(WatchDogData data) {
        if (data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            handleToqueDeQueda(data);
        } else {
            handleAccordingToConditions(data);
        }
        logTime(data);
        data.setNeedToBlock(false);
        handleNotification(data); // this at the end as it needs the processed data
    }

    private void handleNotification(WatchDogData data) {
        data.setEstaNotif(ForegroundAppChecker.POSITIVO);
        data.getWatchDogHandler().onNotificacionPositiva(data.getTimeLogHandler(), data.getPackageName(), data.getTiempoAcumulado() + data.getTiempoImportado(), data.getProporcion(), notification -> {
            data.setNotification(notification);
        });
        data.setUpdated(true);
    }

    private void handleToqueDeQueda(WatchDogData data) {
        if (data.getMisPreferencias().getBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE.getDefaultState())) {
            pointsUpdater.decreasePoints(data);
        } else if (data.getMisPreferencias().getBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM.getDefaultState())) {
            pointsUpdater.keepPoints(data);
        } else {
            handleAccordingToConditions(data);
        }
    }

    private void handleAccordingToConditions(WatchDogData data) {
        data.getPackageConditionsChecker().onAllConditionsMet(data.getPackageName(), areMet -> {
            if (areMet) {
                pointsUpdater.increasePoints(data);
            } else {
                pointsUpdater.keepPoints(data);
                //data.getConditionToaster().noticeMessage(data.getService().getApplication().getResources().getString(R.string.conditions_not_met));
            }
        }, msg -> {
            data.getConditionToaster().noticeMessage(msg);
        });
    }

    private void logTime(WatchDogData data) {
        try {
            data.getTimeLogHandler().insertTimeGoodApp(data.getPackageName(), data.getMilisTranscurridos());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
