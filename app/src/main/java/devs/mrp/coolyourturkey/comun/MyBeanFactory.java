package devs.mrp.coolyourturkey.comun;

import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.watchdog.TimePusherFactory;
import devs.mrp.coolyourturkey.watchdog.TimePusherFactoryInterface;
import devs.mrp.coolyourturkey.watchdog.WatchDogDataFactory;
import devs.mrp.coolyourturkey.watchdog.WatchDogDataFactoryInterface;
import devs.mrp.coolyourturkey.watchdog.actionchain.ActionRequestorFactory;
import devs.mrp.coolyourturkey.watchdog.actionchain.ActionRequestorFactoryInterface;

public class MyBeanFactory {
    private static WatchDogDataFactoryInterface mWatchDogDataFactory;
    public static WatchDogDataFactoryInterface getWatchDogDataFactory() {
        if (mWatchDogDataFactory == null) {
            mWatchDogDataFactory = new WatchDogDataFactory();
        }
        return mWatchDogDataFactory;
    }

    private static TimePusherFactoryInterface mTimePusherFactory;
    public static TimePusherFactoryInterface getTimePusherFactory() {
        if (mTimePusherFactory == null) {
            mTimePusherFactory = new TimePusherFactory();
        }
        return mTimePusherFactory;
    }

    private static ActionRequestorFactoryInterface mActionRequestorFactory;
    public static ActionRequestorFactoryInterface getActionRequestorFactory() {
        if (mActionRequestorFactory == null) {
            mActionRequestorFactory = new ActionRequestorFactory();
        }
        return mActionRequestorFactory;
    }

    public static ConditionNegativeToGroup getNewNegativeCondition() {
        return new ConditionNegativeToGroup();
    }
}
