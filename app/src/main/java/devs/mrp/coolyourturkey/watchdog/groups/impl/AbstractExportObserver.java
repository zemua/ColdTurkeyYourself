package devs.mrp.coolyourturkey.watchdog.groups.impl;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import devs.mrp.coolyourturkey.watchdog.groups.ExportObserver;

public abstract class AbstractExportObserver<T> implements ExportObserver<T> {

    protected Context mContext;
    protected LifecycleOwner mOwner;

    protected List<LiveData<List<T>>> mLiveData = new ArrayList<>();

    @Override
    public void observe(int groupId, long offsetDays, Observer<List<T>> observer) {
        initializeLiveData(offsetDays, groupId).observe(mOwner, observer);
    }

    @Override
    public void stop() {
        mLiveData.forEach(ld -> ld.removeObservers(mOwner));
        mLiveData.clear();
    }

    protected abstract LiveData<List<T>> initializeLiveData(long offsetDays, int groupId);
}
