package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlockRepository;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.ANegativeCheckSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.APositiveCheckSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.dtos.timeblock.FTimeBlockWithSelectableChecks;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public abstract class ASelectablesFacade implements Feedbacker<List<APositiveCheckSelectable>>, MyObservable<List<ANegativeCheckSelectable>> {

    public static final int FEEDBACK_POSITIVAS = 43;
    public static final String FEEDBACK_NEGATIVAS = "feedback blocks negativos";

    protected CheckTimeBlockRepository blocksrepo;
    protected RandomCheckRepository checksRepo;
    protected LifecycleOwner owner;
    private List<FeedbackListener<List<APositiveCheckSelectable>>> feedbackListeners = new ArrayList<>();
    private List<MyObserver<List<ANegativeCheckSelectable>>> myObservers = new ArrayList<>();
    protected FTimeBlockWithSelectableChecks selectablesFactory = new FTimeBlockWithSelectableChecks();
    protected CheckFactory checkFactory = new CheckFactory();

    public ASelectablesFacade(Application app, LifecycleOwner owner) {
        this.blocksrepo = CheckTimeBlockRepository.getRepo(app);
        this.owner = owner;
    }

    public abstract void getPositiveSelectablesOf(Integer blockId);

    public abstract void getNegativeSelectablesOf(Integer blockId);

    @Override
    public void addObserver(MyObserver<List<ANegativeCheckSelectable>> observer) {
        myObservers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, List<ANegativeCheckSelectable> feedback) {
        myObservers.stream().forEach(o -> o.callback(tipo, feedback));
    }

    @Override
    public void giveFeedback(int tipo, List<APositiveCheckSelectable> feedback) {
        feedbackListeners.forEach(o -> o.giveFeedback(tipo, feedback));
    }

    @Override
    public void addFeedbackListener(FeedbackListener<List<APositiveCheckSelectable>> listener) {
        feedbackListeners.add(listener);
    }
}
