package devs.mrp.coolyourturkey.watchdog.groups.impl;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import androidx.lifecycle.Observer;

import java.util.List;
import java.util.Objects;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.watchdog.groups.ExportObserver;

public class AppUsageExportObserver implements ExportObserver <TimeLogger> {

    private Context mContext;
    private LifecycleOwner mOwner;
    private LiveData<List<TimeLogger>> mLiveData;
    private TimeLoggerRepository timeLoggerRepository;

    public AppUsageExportObserver(Context context, LifecycleOwner owner, Application app) {
        mContext = context;
        mOwner = owner;
        timeLoggerRepository = TimeLoggerRepository.getRepo(app);
    }

    @Override
    public void observe(int groupId, long offsetDays, Observer<List<TimeLogger>> observer) {
        initializeLiveData(offsetDays, groupId);
        mLiveData.observe(mOwner, observer);
    }

    @Override
    public void stop() {
        if (!Objects.isNull(mLiveData)) {
            mLiveData.removeObservers(mOwner);
        }
    }

    private void initializeLiveData(long offsetDays, int groupId) {
        stop();
        mLiveData = timeLoggerRepository.findByNewerThanAndGroupId(MilisToTime.beginningOfOffsetDaysConsideringChangeOfDay(offsetDays, mContext), groupId);
    }
}
