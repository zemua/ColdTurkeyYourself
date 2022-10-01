package devs.mrp.coolyourturkey.watchdog.groups;

import androidx.lifecycle.Observer;

import java.util.List;

public interface ExportObserver <T> {
    public void observe(int groupId, long offsetDays, Observer<List<T>> observer);
    public void stop();
}
