package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
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

    private String TAG = "GeneralConditionChecker";

    public GeneralConditionChecker(Application app, LifecycleOwner owner, GrupoConditionRepository conditionRepository) {
        this.app = app;
        this.owner = owner;
        checkers = Arrays.asList(new LocalRecordsChecker(app, owner), new FileChecker(app, owner), new LocalRandomChecksChecker(app, owner));
        this.conditionRepository = conditionRepository;
    }

    private void observeOnTimeCounted(Iterator<ConditionChecker> checkers, GrupoCondition condition, long result, Consumer<Long> action) {
        if (checkers.hasNext()) {
            ConditionChecker checker = checkers.next();
            checker.onTimeCounted(condition, longResult -> {
                observeOnTimeCounted(checkers, condition, result+longResult, action);
            });
        } else {
            action.accept(result);
        }
    }

    @Override
    public void onTimeCounted(GrupoCondition condition, Consumer<Long> action) {
        observeOnTimeCounted(checkers.listIterator(), condition, 0L, action);
    }

    private void observeOnConditionMet(Iterator<ConditionChecker> checkers, GrupoCondition condition, long result, Consumer<Boolean> action) {
        if (checkers.hasNext()) {
            ConditionChecker checker = checkers.next();
            checker.onTimeCounted(condition, longResult -> {
                observeOnConditionMet(checkers, condition, result+longResult, action);
            });
        } else {
            action.accept(result >= MilisToTime.getMilisDeMinutos(condition.getConditionalminutes()));
        }
    }

    @Override
    public void onConditionMet(GrupoCondition condition, Consumer<Boolean> action) {
        observeOnConditionMet(checkers.listIterator(), condition, 0L, action);
    }

    private void observeOnAllConditionsMet(Iterator<GrupoCondition> conditions, Consumer<Boolean> action) {
        if (conditions.hasNext()) {
            GrupoCondition condition = conditions.next();
            onConditionMet(condition, bool -> {
                if (!bool) {
                    action.accept(false);
                } else {
                    observeOnAllConditionsMet(conditions, action);
                }
            });
        } else {
            // didn't found unmet conditions
            action.accept(true);
        }
    }

    @Override
    public void onAllConditionsMet(int groupId, Consumer<Boolean> action) {
        if (groupId < 1) {
            Log.d(TAG, "No group assigned as groupId " + groupId + ", running action with return value 'true'");
            action.accept(true);
            return;
        }
        LiveData<List<GrupoCondition>> cons = conditionRepository.findConditionsByGroupId(groupId);
        cons.observe(owner, conditions -> {
            cons.removeObservers(owner);
            observeOnAllConditionsMet(conditions.listIterator(), action);
        });
    }
}
