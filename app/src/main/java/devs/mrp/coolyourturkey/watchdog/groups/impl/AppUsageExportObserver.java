package devs.mrp.coolyourturkey.watchdog.groups.impl;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;

public class AppUsageExportObserver extends AbstractExportObserver <TimeLogger> {

    private TimeLoggerRepository timeLoggerRepository;

    public AppUsageExportObserver(Context context, LifecycleOwner owner, Application app) {
        mContext = context;
        mOwner = owner;
        timeLoggerRepository = TimeLoggerRepository.getRepo(app);
    }

    @Override
    protected LiveData<List<TimeLogger>> initializeLiveData(long offsetDays, int groupId) {
        LiveData<List<TimeLogger>> ld = timeLoggerRepository.findByNewerThanAndGroupId(MilisToTime.beginningOfOffsetDaysConsideringChangeOfDay(offsetDays, mContext), groupId);
        mLiveData.add(ld);
        return ld;
    }
}
