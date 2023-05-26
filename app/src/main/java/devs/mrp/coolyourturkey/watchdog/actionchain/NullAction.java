package devs.mrp.coolyourturkey.watchdog.actionchain;

import devs.mrp.coolyourturkey.watchdog.ForegroundAppChecker;
import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public class NullAction extends AbstractHandler{
    public NullAction(PointsUpdater pointsUpdater) {
        super(pointsUpdater);
    }

    @Override
    protected boolean canHandle(int tipo) {
        if (tipo == ForegroundAppChecker.NULL) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(WatchDogData data) {
        data.setEstaNotif(ForegroundAppChecker.NULL);
        pointsUpdater.keepPoints(data);
        if (data.getEstaNotif() != data.getUltimanotif() || !data.getUltimoNombre().equals(data.getPackageName()) || Math.abs(data.getTiempoAcumulado() - data.getUltimoAcumulado()) > data.getTimeDifferenceToUpdate() || data.getWasPausado()) {
            data.setNotification(data.getWatchDogHandler().getNotificacionNeutra(data.getPackageName(), data.getTiempoAcumulado() + data.getTiempoImportado(), data.getProporcion()));
            data.setUpdated(true);
        } else {
            data.setUpdated(false);
        }
    }
}
