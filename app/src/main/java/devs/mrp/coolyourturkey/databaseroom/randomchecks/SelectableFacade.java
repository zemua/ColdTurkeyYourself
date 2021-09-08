package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;

public class SelectableFacade extends ASelectablesFacade {

    public SelectableFacade(Application app, LifecycleOwner owner) {
        super(app, owner);
    }

    @Override
    public void getPositiveSelectablesOf(Integer blockId) {
        blocksrepo.getTimeBlockWithChecksById(blockId).observe(owner, new Observer<List<TimeBlockWithChecks>>() {
            @Override
            public void onChanged(List<TimeBlockWithChecks> timeBlockWithChecks) {
                checksRepo.getPositiveChecks().observe(owner, new Observer<List<RandomCheck>>() {
                    @Override
                    public void onChanged(List<RandomCheck> randomChecks) {
                        if (timeBlockWithChecks.size() > 0) {
                            giveFeedback(FEEDBACK_POSITIVAS, selectablesFactory.positiveSelectablesFrom(
                                    checkFactory.importPositivesFrom(randomChecks),
                                    checkFactory.importPositivesFrom(timeBlockWithChecks.get(0).getChecks()
                                            .stream()
                                            .filter(c -> c.getType().equals(RandomCheck.CheckType.POSITIVE))
                                            .collect(Collectors.toList())))
                            );
                        } else {
                            giveFeedback(FEEDBACK_POSITIVAS, selectablesFactory.positiveSelectablesFrom(checkFactory.importPositivesFrom(randomChecks)));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void getNegativeSelectablesOf(Integer blockId) {
        blocksrepo.getTimeBlockWithChecksById(blockId).observe(owner, new Observer<List<TimeBlockWithChecks>>() {
            @Override
            public void onChanged(List<TimeBlockWithChecks> timeBlockWithChecks) {
                checksRepo.getNegativeChecks().observe(owner, new Observer<List<RandomCheck>>() {
                    @Override
                    public void onChanged(List<RandomCheck> randomChecks) {
                        if (timeBlockWithChecks.size() > 0) {
                            doCallBack(FEEDBACK_NEGATIVAS, selectablesFactory.negativeSelectablesFrom(
                                    checkFactory.importNegativesFrom(randomChecks),
                                    checkFactory.importNegativesFrom(timeBlockWithChecks.get(0).getChecks()
                                            .stream()
                                            .filter(c -> c.getType().equals(RandomCheck.CheckType.NEGATIVE))
                                            .collect(Collectors.toList())))
                            );
                        } else {
                            doCallBack(FEEDBACK_NEGATIVAS, selectablesFactory.negativeSelectablesFrom(checkFactory.importNegativesFrom(randomChecks)));
                        }
                    }
                });
            }
        });
    }
}
