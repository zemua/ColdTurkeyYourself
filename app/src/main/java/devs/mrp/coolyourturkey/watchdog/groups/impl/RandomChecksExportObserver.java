package devs.mrp.coolyourturkey.watchdog.groups.impl;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;

public class RandomChecksExportObserver extends AbstractExportObserver<TimeBlockLogger> {

    private TimeBlockLoggerRepository loggerRepository;

    public RandomChecksExportObserver(Context context, LifecycleOwner owner, Application app) {
        mContext = context;
        mOwner = owner;
        loggerRepository = TimeBlockLoggerRepository.getRepo(app);
    }

    @Override
    protected LiveData<List<TimeBlockLogger>> initializeLiveData(long offsetDays, int groupId) {
        LiveData<List<TimeBlockLogger>> ld = loggerRepository.findByTimeNewerAndGroupId(MilisToTime.beginningOfOffsetDaysConsideringChangeOfDay(offsetDays, mContext), groupId);
        mLiveData.add(ld);
        return ld;
    }
}
