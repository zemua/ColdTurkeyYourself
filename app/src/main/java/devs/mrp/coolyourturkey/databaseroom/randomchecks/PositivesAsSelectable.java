package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.dtos.randomcheck.APositiveCheckSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.dtos.timeblock.FTimeBlockWithSelectableChecks;

public class PositivesAsSelectable implements IPositiveAsSelectable{

    private RandomCheckRepository mRepo;
    private LifecycleOwner mOwner;
    private List<MyObserver<List<APositiveCheckSelectable>>> observers = new ArrayList<>();
    protected CheckFactory checkFactory = new CheckFactory();
    protected FTimeBlockWithSelectableChecks selectablesFactory = new FTimeBlockWithSelectableChecks();

    public PositivesAsSelectable(Application app, LifecycleOwner owner) {
        this.mRepo = RandomCheckRepository.getRepo(app);
        this.mOwner = owner;
    }

    @Override
    public void addObserver(MyObserver<List<APositiveCheckSelectable>> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, List<APositiveCheckSelectable> feedback) {
        observers.forEach(o -> o.callback(tipo, feedback));
    }

    @Override
    public void getPositiveSelectables(String tagForCallback) {
        mRepo.getPositiveChecks().observe(
                mOwner,
                checks -> doCallBack(tagForCallback, selectablesFactory.positiveSelectablesFrom(checkFactory.importPositivesFrom(checks))));
    }
}
