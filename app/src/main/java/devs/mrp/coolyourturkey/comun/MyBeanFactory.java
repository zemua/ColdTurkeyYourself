package devs.mrp.coolyourturkey.comun;

import devs.mrp.coolyourturkey.watchdog.WatchDogDataFactory;
import devs.mrp.coolyourturkey.watchdog.WatchDogDataFactoryInterface;

public class MyBeanFactory {
    private static WatchDogDataFactoryInterface mWatchDogDataFactory;
    public static WatchDogDataFactoryInterface getWatchDogDataFactory() {
        if (mWatchDogDataFactory == null) {
            mWatchDogDataFactory = new WatchDogDataFactory();
        }
        return mWatchDogDataFactory;
    }
}
