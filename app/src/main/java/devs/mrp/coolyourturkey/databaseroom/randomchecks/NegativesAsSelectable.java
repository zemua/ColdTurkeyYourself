package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.dtos.randomcheck.ANegativeCheckSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.dtos.timeblock.FTimeBlockWithSelectableChecks;

public class NegativesAsSelectable implements INegativeAsSelectable {

    private RandomCheckRepository mRepo;
    private LifecycleOwner mOwner;
    private List<MyObserver<List<ANegativeCheckSelectable>>> observers = new ArrayList<>();
    protected CheckFactory checkFactory = new CheckFactory();
    protected FTimeBlockWithSelectableChecks selectablesFactory = new FTimeBlockWithSelectableChecks();

    public NegativesAsSelectable(Application app, LifecycleOwner owner) {
        this.mRepo = RandomCheckRepository.getRepo(app);
        this.mOwner = owner;
    }

    @Override
    public void addObserver(MyObserver<List<ANegativeCheckSelectable>> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, List<ANegativeCheckSelectable> feedback) {
        observers.forEach(o -> o.callback(tipo, feedback));
    }

    @Override
    public void getNegativeSelectables(String tagForCallback) {
        mRepo.getNegativeChecks().observe(mOwner, checks -> doCallBack(tagForCallback, selectablesFactory.negativeSelectablesFrom(checkFactory.importNegativesFrom(checks))));
    }
}
