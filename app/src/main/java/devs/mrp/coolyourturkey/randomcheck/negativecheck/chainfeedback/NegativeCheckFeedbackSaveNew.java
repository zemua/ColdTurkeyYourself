package devs.mrp.coolyourturkey.randomcheck.negativecheck.chainfeedback;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.randomcheck.negativecheck.NegativeChecksFragment;

public class NegativeCheckFeedbackSaveNew extends ChainHandler<Check> {

    private Activity mActivity;

    public NegativeCheckFeedbackSaveNew(Activity a) {
        mActivity = a;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(NegativeChecksFragment.FEEDBACK_SAVE_NEW);
    }

    @Override
    protected void handle(Check data) {
        RandomCheckRepository repo = RandomCheckRepository.getRepo(mActivity.getApplication());
        repo.insertNewNegative(data);
        mActivity.finish();
    }
}
