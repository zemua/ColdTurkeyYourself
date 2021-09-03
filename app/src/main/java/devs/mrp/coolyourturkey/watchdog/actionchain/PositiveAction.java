package devs.mrp.coolyourturkey.watchdog.actionchain;

import android.util.Log;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class PositiveAction extends AbstractHandler{

    private final String TAG = "PositiveAction";

    @Override
    protected boolean canHandle(int tipo) {
        if (tipo == ForegroundAppChecker.POSITIVO) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(WatchDogData data) {
        data.setEstaNotif(ForegroundAppChecker.POSITIVO);
        if (data.getUltimoContador() != null) {
            Log.d(TAG, "set tiempo acumulado " + data.getUltimoContador().getAcumulado() + " + " + data.getMilisTranscurridos());
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado() + data.getMilisTranscurridos());
        }
        data.setNotification(data.getWatchDogHandler().getNotificacionPositiva(data.getTimeLogHandler(), data.getPackageName(), data.getTiempoAcumulado() + data.getTiempoImportado(), data.getProporcion()));
        data.setUpdated(true);
        if (!data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            if (data.getTimeLogHandler().ifAllAppConditionsMet(data.getPackageName()) && !data.getTimeLogHandler().ifLimitsReachedForAppName(data.getPackageName())) {
                Log.d(TAG, "push tiempo " + data.getTiempoAcumulado());
                data.getTimePusher().push(data.getNow(), data.getTiempoAcumulado());
            } else {
                // notify if conditions to sum are not met
                if (data.getMisPreferencias().getNotifyConditionsNotMet() && !data.getTimeLogHandler().ifAllAppConditionsMet(data.getPackageName())) {data.getConditionToaster().noticeMessage(data.getService().getApplication().getResources().getString(R.string.conditions_not_met));}
                // notify if limits for today are met
                else if (data.getMisPreferencias().getNotifyLimitesReached() && data.getTimeLogHandler().ifLimitsReachedForAppName(data.getPackageName())) {data.getConditionToaster().noticeMessage(data.getService().getApplication().getResources().getString(R.string.has_alcanzado_el_limite_de_puntos));}
            }
            try {data.getTimeLogHandler().insertTimeGoodApp(data.getPackageName(), data.getMilisTranscurridos());} catch (Exception e) {e.printStackTrace();}
        }
    }
}
