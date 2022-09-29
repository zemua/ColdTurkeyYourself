package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionChecker;

public class LocalRandomChecksChecker implements ConditionChecker {

    private TimeBlockLoggerRepository checkRepository;
    private LifecycleOwner owner;
    private Context context;

    public LocalRandomChecksChecker(Application app, LifecycleOwner owner) {
        checkRepository = TimeBlockLoggerRepository.getRepo(app);
        this.owner = owner;
        this.context = app;
    }

    @Override
    public void onTimeCounted(GrupoCondition condition, Consumer<Long> action) {
        long from = MilisToTime.beginningOfOffsetDaysConsideringChangeOfDay(condition.getFromlastndays(), context);
        LiveData<List<TimeBlockLogger>> loggers = checkRepository.findByTimeNewerAndGroupId(from, condition.getConditionalgroupid());
        loggers.observe(owner, timeLoggers -> {
            loggers.removeObservers(owner);
            long result = timeLoggers.stream().mapToLong(e -> e.getTimecounted()).sum();
            action.accept(result);
        });
    }

}
