package devs.mrp.coolyourturkey.watchdog.actionchain.impl;

import devs.mrp.coolyourturkey.watchdog.WatchDogData;
import devs.mrp.coolyourturkey.watchdog.actionchain.PointsUpdater;

public class PointsUpdaterImpl implements PointsUpdater {

    @Override
    public void decreasePoints(WatchDogData data) {
        if (data.getUltimoContador() != null) {
            long lproporcionMilisTranscurridos = data.getMilisTranscurridos() * data.getProporcion();
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado() - lproporcionMilisTranscurridos);
            data.getTimePusher().push(data.getNow(), data.getTiempoAcumulado());
        }
    }

    @Override
    public void increasePoints(WatchDogData data) {
        if (data.getUltimoContador() != null) {
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado() + data.getMilisTranscurridos());
            data.getTimePusher().push(data.getNow(), data.getTiempoAcumulado());
        }
    }

    @Override
    public void keepPoints(WatchDogData data) {
        if (data.getUltimoContador() != null) {
            data.setTiempoAcumulado(data.getUltimoContador().getAcumulado());
            // no need to push on time pusher
        }
    }
}
