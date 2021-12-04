package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public interface IScheduler {

    public void setTimeBlock(AbstractTimeBlock block);

    public Long schedule(AbstractTimeBlock block, Long schedule);

    public long getNow();

    public boolean outisdeQueryIfOnSchedule(long milis);

}
