package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLogger;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionChecker;

public class LocalRecordsChecker implements ConditionChecker {

    private TimeLoggerRepository loggerRepository;
    private LifecycleOwner owner;

    public LocalRecordsChecker(Application app, LifecycleOwner owner) {
        loggerRepository = TimeLoggerRepository.getRepo(app);
        this.owner = owner;
    }

    @Override
    public void onTimeCounted(GrupoCondition condition, Consumer<Long> action) {
        long from = MilisToTime.offsetDayInMillis(condition.getFromlastndays());
        LiveData<List<TimeLogger>> loggers = loggerRepository.findByNewerThanAndGroupId(from, condition.getConditionalgroupid());
        loggers.observe(owner, timeLoggers -> {
            loggers.removeObservers(owner);
            long result = timeLoggers.stream().mapToLong(e -> e.getCountedtimemilis()).sum();
            action.accept(result);
        });
    }
}
