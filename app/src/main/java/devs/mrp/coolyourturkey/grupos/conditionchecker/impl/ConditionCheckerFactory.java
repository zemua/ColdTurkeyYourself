package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoConditionRepository;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;

public class ConditionCheckerFactory {
    public static ConditionCheckerCommander getConditionChecker(Application app, LifecycleOwner owner) {
        GrupoConditionRepository conditionRepo = GrupoConditionRepository.getRepo(app);
        GrupoRepository grupoRepo = GrupoRepository.getRepo(app);
        return new GeneralConditionChecker(app, owner, conditionRepo, grupoRepo);
    }
}
