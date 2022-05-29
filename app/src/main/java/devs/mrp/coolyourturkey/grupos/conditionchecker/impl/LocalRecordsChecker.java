package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;
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
        loggerRepository.findByNewerThanAndGroupId(from, condition.getConditionalgroupid()).observe(owner, timeLoggers -> {
            long result = timeLoggers.stream().mapToLong(e -> e.getCountedtimemilis()).sum();
            action.accept(result);
        });
    }
}
