package devs.mrp.coolyourturkey.watchdog.actionchain;

import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public abstract class AbstractHandler {

    protected AbstractHandler mNextHandler;
    protected PointsUpdater pointsUpdater;

    public AbstractHandler(PointsUpdater pointsUpdater) {
        this.pointsUpdater = pointsUpdater;
    }

    public void setNextHandler(AbstractHandler handler) {
        mNextHandler = handler;
    }

    protected abstract boolean canHandle(int tipo);

    public void receiveRequest(int tipo, WatchDogData data) {
        if (!canHandle(tipo)) {
            if (mNextHandler != null) {
                mNextHandler.receiveRequest(tipo, data);
            }
            return;
        }
        handle(data);
    }

    protected abstract void handle(WatchDogData data);

}
