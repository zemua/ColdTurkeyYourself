package devs.mrp.coolyourturkey.grupos.timing.impl;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;
import devs.mrp.coolyourturkey.grupos.timing.GroupTimeAssembler;

public class RandomChecksTimeAssembler implements GroupTimeAssembler {

    private LifecycleOwner owner;
    private TimeBlockLoggerRepository loggerRepository;
    private Context mContext;

    public RandomChecksTimeAssembler(LifecycleOwner owner, Application app) {
        this.owner = owner;
        this.loggerRepository = TimeBlockLoggerRepository.getRepo(app);
        this.mContext = app;
    }

    @Override
    public void forGroupToday(int groupId, Consumer<Long> action) {
        forGroupSinceDays(groupId, 0, action);
    }

    @Override
    public void forGroupSinceDays(int groupId, int sinceDays, Consumer<Long> action) {
        long epoch = MilisToTime.beginningOfOffsetDaysConsideringChangeOfDay(sinceDays, mContext);
        loggerRepository.findByTimeNewerAndGroupId(epoch, groupId).observe(owner, entries -> {
            long result = entries.stream().mapToLong(TimeBlockLogger::getTimecounted).sum();
            action.accept(result);
        });
    }
}
