package devs.mrp.coolyourturkey.randomcheck.positivecheck.chainfeedback;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.PositiveChecksFragment;

public class PositiveCheckFeedbackSaveNew extends ChainHandler<PositiveCheck> {

    private Activity activity;

    public PositiveCheckFeedbackSaveNew(Activity c) {
        activity = c;
    }

    @Override
    protected boolean canHandle(String tipo) {
        if (tipo.equals(PositiveChecksFragment.FEEDBACK_SAVE_NEW)) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(PositiveCheck data) {
        RandomCheckRepository repo = RandomCheckRepository.getRepo(activity.getApplication());
        repo.insertNewPositive(data);
        activity.finish();
    }
}
