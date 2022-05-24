package devs.mrp.coolyourturkey.grupos.timing;

import java.util.function.Consumer;

public interface GroupGeneralAssembler {
    public void forGroupToday(int groupId);
    public void forGroupSinceDays(int groupId);
}
