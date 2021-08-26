package devs.mrp.coolyourturkey.randomcheck.negativecheck.chainfeedback;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.randomcheck.negativecheck.NegativeChecksFragment;

public class NegativeCheckFeedbackDelete extends ChainHandler<Check> {

    private Activity mActivity;

    public NegativeCheckFeedbackDelete(Activity a) {
        mActivity = a;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(NegativeChecksFragment.FEEDBACK_DELETE_THIS);
    }

    @Override
    protected void handle(Check data) {
        RandomCheckRepository repo = RandomCheckRepository.getRepo(mActivity.getApplication());
        repo.deleteById(data.getId());
        mActivity.finish();
    }
}
