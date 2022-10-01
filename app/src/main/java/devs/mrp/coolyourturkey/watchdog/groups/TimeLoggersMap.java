package devs.mrp.coolyourturkey.watchdog.groups;

import android.content.Context;

import java.util.List;

public interface TimeLoggersMap<T> {
    public void put(int groupId, List<T> loggers);
    public long get(int groupId, long offsetDays);
}
