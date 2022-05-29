package devs.mrp.coolyourturkey.grupos.conditionchecker;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;

public interface ChangeChecker {
    public void onChangedToMet(int groupId);
}
