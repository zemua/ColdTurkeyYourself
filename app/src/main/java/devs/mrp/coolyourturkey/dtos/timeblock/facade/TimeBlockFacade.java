package devs.mrp.coolyourturkey.dtos.timeblock.facade;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlockRepository;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.TimeBlockFactory;

public class TimeBlockFacade implements ITimeBlockFacade{

    private CheckTimeBlockRepository repo;
    private TimeBlockFactory factory;
    private LifecycleOwner mOwner;

    public TimeBlockFacade(Application app, LifecycleOwner owner) {
        repo = CheckTimeBlockRepository.getRepo(app);
        factory = new TimeBlockFactory();
        mOwner = owner;
    }

    @Override
    public void getById(Integer blockid, MyObserver<List<AbstractTimeBlock>> observer) {
        repo.getTimeBlockWithChecksById(blockid).observe(mOwner, timeBlockWithChecks -> observer.callback("", timeBlockWithChecks.stream().map(tb -> factory.importFrom(tb)).collect(Collectors.toList())));
    }

    @Override
    public void getAll(MyObserver<List<AbstractTimeBlock>> observer) {
        repo.getAllTimeBlockWithChecks().observe(mOwner, timeBlockWithChecks -> observer.callback("", timeBlockWithChecks.stream().map(tb -> factory.importFrom(tb)).collect(Collectors.toList())));
    }
}
