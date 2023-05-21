package devs.mrp.coolyourturkey.watchdog.actionchain;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class PositiveAction extends AbstractHandler{

    private static final String TAG = PositiveAction.class.getSimpleName();

    @Override
    protected boolean canHandle(int tipo) {
        if (tipo == ForegroundAppChecker.POSITIVO) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(WatchDogData data) {
        handleNotification(data);

        if (data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            handleToqueDeQueda(data);
        } else {
            handleAccordingToConditions(data);
        }
        logTime(data);
        data.setNeedToBlock(false);
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
            decreasePoints(data);
        } else if (data.getMisPreferencias().getBoolean(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM, PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM.getDefaultState())) {
            keepPoints(data);
        } else {
            handleAccordingToConditions(data);
        }
    }

    private void decreasePoints(WatchDogData data) {
        if (data.getUltimoContador() != null) {
            long lproporcionMilisTranscurridos = Math.abs(data.getMilisTranscurridos() * data.getProporcion());
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado() - lproporcionMilisTranscurridos);
            data.getTimePusher().push(data.getNow(), data.getTiempoAcumulado());
        }
    }

    // TODO decouple point handling from action handling
    private void keepPoints(WatchDogData data) {
        if (data.getUltimoContador() != null) {
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado());
        }
    }

    private void handleAccordingToConditions(WatchDogData data) {
        data.getTimeLogHandler().onAllConditionsMet(data.getPackageName(), areMet -> {
            if (areMet) {
                increasePoints(data);
            } else {
                keepPoints(data);
                data.getConditionToaster().noticeMessage(data.getService().getApplication().getResources().getString(R.string.conditions_not_met));
            }
        });
    }

    private void increasePoints(WatchDogData data) {
        if (data.getUltimoContador() != null) {
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado() + data.getMilisTranscurridos());
            data.getTimePusher().push(data.getNow(), data.getTiempoAcumulado());
        }
    }

    private void logTime(WatchDogData data) {
        try {
            data.getTimeLogHandler().insertTimeGoodApp(data.getPackageName(), data.getMilisTranscurridos());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
