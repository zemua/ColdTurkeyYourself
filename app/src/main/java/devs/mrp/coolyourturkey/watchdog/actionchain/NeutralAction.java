package devs.mrp.coolyourturkey.watchdog.actionchain;

import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class NeutralAction extends AbstractHandler{

    @Override
    protected boolean canHandle(int tipo) {
        if (tipo == ForegroundAppChecker.NEUTRO) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(WatchDogData data) {
        data.setEstaNotif(ForegroundAppChecker.NEUTRO);
        data.setTiempoAcumulado(data.getUltimoContador().getAcumulado());
        if (data.getEstaNotif() != data.getUltimanotif() || !data.getUltimoNombre().equals(data.getPackageName()) || Math.abs(data.getTiempoAcumulado() - data.getUltimoAcumulado()) > data.getTimeDifferenceToUpdate() || data.getWasPausado() ||data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            data.setNotification(data.getWatchDogHandler().getNotificacionNeutra(data.getPackageName(), data.getTiempoAcumulado() + data.getTiempoImportado(), data.getProporcion()));
            data.setUpdated(true);
        } else {
            data.setUpdated(false);
        }
        try {data.getTimeLogHandler().insertTimeNeutralApp(data.getPackageName(), data.getMilisTranscurridos());} catch (Exception e) {e.printStackTrace();}
    }
}
