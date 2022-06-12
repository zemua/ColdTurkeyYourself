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

public class AppsTimeAssembler implements GroupTimeAssembler {

    private LifecycleOwner owner;
    private TimeLoggerRepository timeLoggerRepository;

    public AppsTimeAssembler(LifecycleOwner owner, Application app) {
        this.owner = owner;
        timeLoggerRepository = TimeLoggerRepository.getRepo(app);
    }

    @Override
    public void forGroupToday(int groupId, Consumer<Long> action) {
        forGroupSinceDays(groupId, 0, action);
    }

    @Override
    public void forGroupSinceDays(int groupId, int sinceDays, Consumer<Long> action) {
        LiveData<List<TimeLogger>> entries = getEntries(sinceDays, groupId);
        entries.observe(owner, loggers -> {
            entries.removeObservers(owner);
            long result = loggers.stream().mapToLong(TimeLogger::getUsedtimemilis).sum();
            action.accept(result);
        });
    }

    private LiveData<List<TimeLogger>> getEntries(int days, int group) {
        long epoch = MilisToTime.offsetDayInMillis(days);
        return timeLoggerRepository.findByNewerThanAndGroupId(epoch, group);
    }
}
