package devs.mrp.coolyourturkey.randomcheck.positivecheck.chainfeedback;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.review.PositiveChecksFragment;

public class PositiveCheckFeedbackDelete extends ChainHandler<PositiveCheck> {

    private Activity mActivity;

    public PositiveCheckFeedbackDelete(Activity a) {
        mActivity = a;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(PositiveChecksFragment.FEEDBACK_DELETE_THIS);
    }

    @Override
    protected void handle(PositiveCheck data) {
        RandomCheckRepository repo = RandomCheckRepository.getRepo(mActivity.getApplication());
        repo.deleteById(data.getId());
        mActivity.finish();
    }
}
