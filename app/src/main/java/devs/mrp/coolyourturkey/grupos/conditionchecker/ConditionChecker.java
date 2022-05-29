package devs.mrp.coolyourturkey.grupos.conditionchecker;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;

public interface ConditionChecker {
    public void onTimeCounted(GrupoCondition condition, Consumer<Long> action);
}
