package devs.mrp.coolyourturkey.grupos.reviewer;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public abstract class ReviewerActivity extends SingleFragmentActivity<Intent> {

    private ActivityResultLauncher<Intent> myLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    // TODO
                }
            }
        });
    }

    @Override
    protected void initListeners(Fragment frgmnt) {
        if (frgmnt instanceof ReviewerFragment) {
            ReviewerFragment fragment = (ReviewerFragment) frgmnt;
            fragment.addFeedbackListener(new FeedbackListener<Intent>() {
                @Override
                public void giveFeedback(int tipo, Intent feedback, Object... args) {
                    switch (tipo) {
                        // TODO init listeners on fragment
                    }
                }
            });
        }
    }

}
