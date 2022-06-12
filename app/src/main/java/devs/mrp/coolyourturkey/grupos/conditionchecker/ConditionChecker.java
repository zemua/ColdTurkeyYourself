package devs.mrp.coolyourturkey.grupos.conditionchecker;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;

public interface ConditionChecker {
    public void onTimeCounted(GrupoCondition condition, Consumer<Long> action);
}
