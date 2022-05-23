package devs.mrp.coolyourturkey.grupos.timing.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.grupos.timing.GroupTimeAssembler;

public class GroupAppsTimeAssembler implements GroupTimeAssembler {

    private LifecycleOwner owner;
    private TimeLoggerRepository timeLoggerRepository;

    public GroupAppsTimeAssembler(LifecycleOwner owner, Application app) {
        this.owner = owner;
        timeLoggerRepository = TimeLoggerRepository.getRepo(app);
    }

    @Override
    public void forGroupToday(int groupId, Consumer<Long> action) {
        forGroupSinceDays(groupId, 0, action);
    }

    @Override
    public void forGroupSinceDays(int groupId, int sinceDays, Consumer<Long> action) {
        getEntries(sinceDays, groupId).observe(owner, loggers -> {
            long result = loggers.stream().mapToLong(TimeLogger::getUsedtimemilis).sum();
            action.accept(result);
        });
    }

    private LiveData<List<TimeLogger>> getEntries(int days, int group) {
        long epoch = MilisToTime.offsetDayInMillis(days);
        return timeLoggerRepository.findByNewerThanAndGroupId(epoch, group);
    }
}
