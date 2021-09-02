package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.FTimeBlockFacade;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.ITimeBlockFacade;

public class CheckManager implements ICheckManager{

    private ITimeBlockFacade mFacade;
    private static CheckManager instance;
    private Map<Integer, Long> mSchedules;
    private Map<Integer, AbstractTimeBlock> mBlocks;
    private Application mApp;
    private LifecycleOwner mOwner;
    private IScheduler mScheduler;

    private CheckManager(Application app, LifecycleOwner owner) {
        mFacade = FTimeBlockFacade.getNew(app, owner);
        mApp = app;
        mOwner = owner;
        mSchedules = new HashMap<>();
        mBlocks = new HashMap<>();
        mScheduler = new Scheduler();
    }

    public static CheckManager getInstance(Application app, LifecycleOwner owner) {
        if (instance == null) {
            instance = new CheckManager(app, owner);
            instance.run();
        }
        return instance;
    }

    @Override
    public void refresh() {

    }

    private void run() {
        // get all time-blocks and set observer
        mFacade.getAll((tipo, blocks) -> {
            // re-schedule as needed sending current saved schedule of time-blocks, or 0 otherwise
            mBlocks = blocks.stream().map(b -> {
                Optional<Long> opt = Optional.of(mSchedules.get(b.getId()));
                mSchedules.put(b.getId(), mScheduler.schedule(b, opt.orElse(0L)));
                return b;
            }).collect(Collectors.toMap(b -> b.getId(), b -> b));
            // remove any schedules of time-blocks that no longer exist
            Iterator<Map.Entry<Integer, Long>> i = mSchedules.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<Integer, Long> next = i.next();
                if (!mBlocks.containsKey(next.getKey())) {
                    i.remove();
                }
            }
        });
    }

}
