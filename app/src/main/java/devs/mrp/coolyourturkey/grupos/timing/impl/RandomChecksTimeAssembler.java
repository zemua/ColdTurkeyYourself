package devs.mrp.coolyourturkey.grupos.timing.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLoggerRepository;
import devs.mrp.coolyourturkey.grupos.timing.GroupTimeAssembler;

public class RandomChecksTimeAssembler implements GroupTimeAssembler {

    private LifecycleOwner owner;
    private TimeBlockLoggerRepository loggerRepository;

    public RandomChecksTimeAssembler(LifecycleOwner owner, Application app) {
        this.owner = owner;
        this.loggerRepository = TimeBlockLoggerRepository.getRepo(app);
    }

    @Override
    public void forGroupToday(int groupId, Consumer<Long> action) {

    }

    @Override
    public void forGroupSinceDays(int groupId, int sinceDays, Consumer<Long> action) {

    }
}
