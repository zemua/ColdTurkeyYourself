package devs.mrp.coolyourturkey.watchdog.actionchain;

import devs.mrp.coolyourturkey.watchdog.WatchDogData;

public interface PointsUpdater {

    public void decreasePoints(WatchDogData data);
    public void increasePoints(WatchDogData data);
    public void keepPoints(WatchDogData data);

}
