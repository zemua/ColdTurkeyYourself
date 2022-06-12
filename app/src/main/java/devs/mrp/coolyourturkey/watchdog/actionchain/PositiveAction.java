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
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado() + data.getMilisTranscurridos());
        }
        data.getWatchDogHandler().onNotificacionPositiva(data.getTimeLogHandler(), data.getPackageName(), data.getTiempoAcumulado() + data.getTiempoImportado(), data.getProporcion(), notification -> {
            data.setNotification(notification);
        });
        data.setUpdated(true);
        if (!data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            data.getTimeLogHandler().onAllConditionsMet(data.getPackageName(), areMet -> {
                if (areMet) {
                    data.getTimePusher().push(data.getNow(), data.getTiempoAcumulado());
                } else {
                    // notify if conditions to sum are not met
                    data.getConditionToaster().noticeMessage(data.getService().getApplication().getResources().getString(R.string.conditions_not_met));
                }
                try {data.getTimeLogHandler().insertTimeGoodApp(data.getPackageName(), data.getMilisTranscurridos());} catch (Exception e) {e.printStackTrace();}
            });
        }
    }
}
