package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.GenericCache;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.comun.impl.TurkeyFactoryProvider;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionChecker;

public class LocalRandomChecksChecker implements ConditionChecker {

    GenericCache<Long,GrupoCondition> conditionTimeSpent = TurkeyFactoryProvider.<Long,GrupoCondition>getGenericCache().getInstance();

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
        Long cached = conditionTimeSpent.get(condition);
        if (Objects.nonNull(cached)) {
            action.accept(cached);
        } else {
            long from = MilisToTime.beginningOfOffsetDaysConsideringChangeOfDay(condition.getFromlastndays(), context);
            LiveData<List<TimeBlockLogger>> loggers = checkRepository.findByTimeNewerAndGroupId(from, condition.getConditionalgroupid());
            loggers.observe(owner, timeLoggers -> {
                loggers.removeObservers(owner);
                long result = timeLoggers.stream().mapToLong(e -> e.getTimecounted()).sum();
                conditionTimeSpent.put(condition, result);
                action.accept(result);
            });
        }
    }

}
