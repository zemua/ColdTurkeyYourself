package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;
import android.os.Handler;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.comun.TimeBounded;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoType;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoConditionRepository;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ChangeChecker;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;

public class ChangeNotifier implements ChangeChecker {

    private final int NOTIFICATION_ID = -32;

    private TimeBounded timer;
    private ConditionCheckerCommander checker;
    private Application app;
    private LifecycleOwner owner;
    private GrupoConditionRepository conditionRepository;
    private GrupoRepository grupoRepo;
    private MisPreferencias preferencias;
    private Notificador notificador;
    private static Map<Integer,Boolean> beforeMap = new HashMap<>();
    private Handler mainHandler;

    ChangeNotifier(Application app, LifecycleOwner owner, ConditionCheckerCommander checker, GrupoConditionRepository conditionRepository, GrupoRepository grupoRepository, TimeBounded timeBounded, MisPreferencias prefs, Notificador notificador, Handler mainHandler) {
        this.owner = owner;
        this.app = app;
        this.checker = checker;
        this.conditionRepository = conditionRepository;
        this.grupoRepo = grupoRepository;
        this.timer = timeBounded;
        this.preferencias = prefs;
        this.notificador = notificador;
        this.mainHandler = mainHandler;
    }

    @Override
    public void onChangedToMet() {
        if (!timer.isTimeExpired()) {
            return;
        }
        mainHandler.post(() -> {
            LiveData<List<Grupo>> gs= grupoRepo.findAllGrupos();
            gs.observe(owner, grupos -> {
                gs.removeObservers(owner);
                grupos.stream().forEach(grupo ->{
                    LiveData<List<GrupoCondition>> cons = conditionRepository.findConditionsByGroupId(grupo.getId());
                    cons.observe(owner, conditions -> {
                        cons.removeObservers(owner);
                        onConditionsMet(conditions, grupo.getId(), grupo.getNombre(), grupo.getType());
                    });
                });
            });
        });
    }

    private void onConditionsMet(List<GrupoCondition> conditions, int groupId, String groupName, GrupoType type) {
        Map<Integer,Boolean> meetMap = new HashMap<>();
        conditions.forEach(c -> checker.onConditionMet(c, booleanResult -> {
            meetMap.put(c.getId(), booleanResult);
            if (meetMap.size() == conditions.size()) {
                boolean result = meetMap.values().stream().filter(b -> b.equals(false)).findAny().orElse(true);
                if (!Optional.ofNullable(beforeMap.get(groupId)).orElse(false) && result) {
                    notifyConditionsChanged(groupName, type);
                }
                beforeMap.put(groupId, result);
            }
        }));
    }

    private void notifyConditionsChanged(String groupName, GrupoType type) {
        if (preferencias.getNotifyConditionsJustMet()) {
            Integer drawable = type.equals(GrupoType.NEGATIVE) ? R.drawable.bug : R.drawable.plus_circle_outline;
            String description = app.getString(R.string.ahora_cumple_las_condiciones);
            notificador.createNotification(drawable, groupName, description, Notificador.CONDITION_MET_CHANNEL_ID, NOTIFICATION_ID);
        }
    }
}
