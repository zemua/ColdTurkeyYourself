package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoConditionRepository;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionChecker;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;

public class GeneralConditionChecker implements ConditionCheckerCommander {

    private List<ConditionChecker> checkers;
    private Application app;
    private LifecycleOwner owner;
    private GrupoConditionRepository conditionRepository;
    private GrupoRepository grupoRepository;

    private String TAG = "GeneralConditionChecker";

    public GeneralConditionChecker(Application app, LifecycleOwner owner, GrupoConditionRepository conditionRepository, GrupoRepository grupoRepository) {
        this.app = app;
        this.owner = owner;
        checkers = Arrays.asList(new LocalRecordsChecker(app, owner), new FileChecker(app, owner), new LocalRandomChecksChecker(app, owner));
        this.conditionRepository = conditionRepository;
        this.grupoRepository = grupoRepository;
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

    private void observeOnConditionMet(Iterator<ConditionChecker> checkers, GrupoCondition condition, long result, BiConsumer<Boolean,Long> action) {
        if (checkers.hasNext()) {
            ConditionChecker checker = checkers.next();
            checker.onTimeCounted(condition, longResult -> {
                observeOnConditionMet(checkers, condition, result+longResult, action);
            });
        } else {
            action.accept(result >= MilisToTime.getMilisDeMinutos(condition.getConditionalminutes()), result);
        }
    }

    @Override
    public void onConditionMet(GrupoCondition condition, Consumer<Boolean> action) {
        onConditionMet(condition, (aBoolean, aLong) -> action.accept(aBoolean));
    }

    public void onConditionMet(GrupoCondition condition, BiConsumer<Boolean,Long> action) {
        LiveData<List<Grupo>> grupoLiveData = grupoRepository.findGrupoById(condition.getConditionalgroupid());
        grupoLiveData.observe(this.owner, groups -> {
            Grupo grupo = null;
            if (groups.size() > 0) {
                grupo = groups.get(0);
            }
            if (Objects.nonNull(grupo) && grupo.isIgnoreBasedConditions()) {
                action.accept(true, 0L);
            } else {
                observeOnConditionMet(checkers.listIterator(), condition, 0L, action);
            }
        });
    }

    private void observeOnAllConditionsMet(Iterator<GrupoCondition> conditions, Consumer<Boolean> action, Consumer<String> message) {
        if (conditions.hasNext()) {
            GrupoCondition condition = conditions.next();
            onConditionMet(condition, (bool, time) -> {
                if (!bool) {
                    action.accept(false);
                    // dirty hack to pop up a message if there are other conditions not met
                    // The action is set to do nothing else, only the message takes effect
                    sendMessage(condition, message, time);
                    observeOnAllConditionsMet(conditions, (a) -> {}, message);
                } else {
                    observeOnAllConditionsMet(conditions, action, message);
                }
            });
        } else {
            // didn't found unmet conditions
            action.accept(true);
        }
    }

    public void sendMessage(GrupoCondition condition, Consumer<String> message, Long timeMillis) {
        LiveData<List<Grupo>> grupoLiveData = grupoRepository.findGrupoById(condition.getConditionalgroupid());
        grupoLiveData.observe(this.owner, groups -> {
            Grupo grupo = null;
            if (groups.size() > 0) {
                grupo = groups.get(0);
            }
            if (Objects.nonNull(grupo)) {
                message.accept(app.getString(R.string.conditions_not_met)
                        + ": "
                        + grupo.getNombre()
                        + " "
                        + condition.getFromlastndays()
                        + app.getString(R.string.dias)
                        + ": "
                        + MilisToTime.getMinutes(timeMillis)
                        + " "
                        + app.getString(R.string.de)
                        + " "
                        + condition.getConditionalminutes()
                        + " "
                        + app.getString(R.string.minutos)
                );
            }
        });
    }

    @Override
    public void onAllConditionsMet(int groupId, Consumer<Boolean> action) {
        onAllConditionsMet(groupId, action, (s) -> {});
    }

    @Override
    public void onAllConditionsMet(int groupId, Consumer<Boolean> action, Consumer<String> message) {
        if (groupId < 1) {
            Log.d(TAG, "No group assigned as groupId " + groupId + ", running action with return value 'true'");
            action.accept(true);
            return;
        }
        LiveData<List<GrupoCondition>> cons = conditionRepository.findConditionsByGroupId(groupId);
        cons.observe(owner, conditions -> {
            cons.removeObservers(owner);
            observeOnAllConditionsMet(conditions.listIterator(), action, message);
        });
    }
}
