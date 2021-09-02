package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.FTimeBlockFacade;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.ITimeBlockFacade;

public class CheckManager implements ICheckManager{

    private ITimeBlockFacade mFacade;
    private static CheckManager instance;
    private Map<AbstractTimeBlock, Long> mBlocksSchedule;
    private Application mApp;
    private LifecycleOwner mOwner;

    private CheckManager(Application app, LifecycleOwner owner) {
        mFacade = FTimeBlockFacade.getNew(app, owner);
        mApp = app;
        mOwner = owner;
        mBlocksSchedule = new LinkedHashMap<>();
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
        mFacade.getAll((tipo, blocks) -> {
            mBlocksSchedule.clear();
        });
    }

}
