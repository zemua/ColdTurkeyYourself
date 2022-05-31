package devs.mrp.coolyourturkey.grupos.conditionchecker;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;

public interface ConditionCheckerCommander {
    public void onTimeCounted(GrupoCondition condition, Consumer<Long> action);
    public void onConditionMet(GrupoCondition condition, Consumer<Boolean> action);
    public void onAllConditionsMet(int groupID, Consumer<Boolean> action);
}
