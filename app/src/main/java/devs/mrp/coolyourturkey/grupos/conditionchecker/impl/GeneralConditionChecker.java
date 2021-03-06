package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoConditionRepository;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionChecker;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;

public class GeneralConditionChecker implements ConditionCheckerCommander {

    private List<ConditionChecker> checkers;
    private Application app;
    private LifecycleOwner owner;
    private GrupoConditionRepository conditionRepository;

    public GeneralConditionChecker(Application app, LifecycleOwner owner, GrupoConditionRepository conditionRepository) {
        this.app = app;
        this.owner = owner;
        checkers = Arrays.asList(new LocalRecordsChecker(app, owner), new FileChecker(app, owner));
        this.conditionRepository = conditionRepository;
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

    @Override
    public void onAllConditionsMet(int groupId, Consumer<Boolean> action) {
        LiveData<List<GrupoCondition>> cons = conditionRepository.findConditionsByGroupId(groupId);
        cons.observe(owner, conditions -> {
            if (conditions.size() == 0) {
                cons.removeObservers(owner);
                action.accept(true); // no conditions for group, so true, all met
            }
            Set<Integer> recorded = new HashSet<>();
            AtomicBoolean result = new AtomicBoolean(true);
            conditions.forEach(condition -> onConditionMet(condition, bool -> {
                if (recorded.contains(condition.getId())) {
                    return;
                }
                recorded.add(condition.getId());
                if (!bool) {
                    result.set(false);
                }
                if (recorded.size() >= conditions.size() || !result.get()) {
                    cons.removeObservers(owner);
                    action.accept(result.get());
                }
            }));
        });
    }
}
