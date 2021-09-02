package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public interface IScheduler {

    public Long schedule(AbstractTimeBlock block);

}
