package devs.mrp.coolyourturkey.grupos.timing;

import java.util.function.Consumer;

public interface GroupTimeAssembler {

    public void forGroupToday(int groupId, Consumer<Long> action);
    public void forGroupSinceDays(int groupId, int sinceDays, Consumer<Long> action);

}
