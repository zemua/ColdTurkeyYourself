package devs.mrp.coolyourturkey.randomcheck.positivecheck.chainfeedback;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.review.PositiveChecksFragment;

public class PositiveCheckFeedbackSaveExisting extends ChainHandler<PositiveCheck> {

    private Activity activity;

    public PositiveCheckFeedbackSaveExisting(Activity c) {
        activity = c;
    }

    @Override
    protected boolean canHandle(String tipo) {
        if (tipo.equals(PositiveChecksFragment.FEEDBACK_SAVE_EXISTING)) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(PositiveCheck data) {
        RandomCheckRepository repo = RandomCheckRepository.getRepo(activity.getApplication());
        repo.replacePositive(data);
        activity.finish();
    }

}
