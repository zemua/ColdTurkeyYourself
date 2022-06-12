package devs.mrp.coolyourturkey.watchdog.actionchain;

import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.TimeToaster;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class NegativeAction extends AbstractHandler{

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
        // discount time on data
        if (data.getUltimoContador() != null) {
            long lproporcionMilisTranscurridos = data.getMilisTranscurridos() * data.getProporcion();
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado() - lproporcionMilisTranscurridos);
        }
        // update notification info
        if (data.getEstaNotif() != data.getUltimanotif() || !data.getUltimoNombre().equals(data.getPackageName()) || Math.abs(data.getTiempoAcumulado() - data.getUltimoAcumulado()) > data.getTimeDifferenceToUpdate() || data.getWasPausado() || data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            data.setNotification(data.getWatchDogHandler().getNotificacionNegativa(data.getPackageName(), data.getTiempoAcumulado() + data.getTiempoImportado(), data.getProporcion()));
            data.setUpdated(true);
        } else {
            data.setUpdated(false);
        }
        // block screen when needed
        if (!data.getScreenBlock().estamosBloqueando()) {
            data.getTimePusher().push(data.getNow(), data.getTiempoAcumulado());
            try {data.getTimeLogHandler().insertTimeBadApp(data.getPackageName(), data.getMilisTranscurridos());} catch (Exception e) {e.printStackTrace();}
        }
        // send notice when needed
        new TimeToaster(data.getService().getApplication()).noticeTimeLeft((data.getTiempoAcumulado() + data.getTiempoImportado()) / data.getProporcion());
    }
}
