package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionChecker;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;

public class GeneralConditionChecker implements ConditionCheckerCommander {

    private List<ConditionChecker> checkers;
    private Application app;
    private LifecycleOwner owner;

    public GeneralConditionChecker(Application app, LifecycleOwner owner) {
        this.app = app;
        this.owner = owner;
        checkers = Arrays.asList(new LocalRecordsChecker(app, owner), new FileChecker(app, owner));
    }

    @Override
    public void onTimeCounted(GrupoCondition condition, Consumer<Long> action) {
        Set<String> recorded = new HashSet<>();
        AtomicLong result = new AtomicLong(0L);
        checkers.forEach(checker -> checker.onTimeCounted(condition, longResult -> {
            if (recorded.contains(checker.getClass().getSimpleName())) {
                return;
            }
            recorded.add(checker.getClass().getSimpleName());
            long res = result.addAndGet(longResult);
            if (recorded.size() == checkers.size()) {
                action.accept(res);
            }
        }));
    }

    @Override
    public void onConditionMet(GrupoCondition condition, Consumer<Boolean> action) {
        Set<String> recorded = new HashSet<>();
        AtomicLong result = new AtomicLong(0L);
        checkers.forEach(checker -> checker.onTimeCounted(condition, longResult -> {
            if (recorded.contains(checker.getClass().getSimpleName())) {
                return;
            }
            recorded.add(checker.getClass().getSimpleName());
            long res = result.addAndGet(longResult);
            if (recorded.size() == checkers.size()) {
                action.accept(res >= MilisToTime.getMilisDeMinutos(condition.getConditionalminutes()));
            }
        }));
    }
}
