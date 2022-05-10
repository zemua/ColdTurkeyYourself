package devs.mrp.coolyourturkey.grupos.reviewer;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoType;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public class ReviewerActivity extends SingleFragmentActivity<Intent> {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";
    public static final String EXTRA_GROUP_TYPE = "extra_group_type";

    private static final String TAG = "ReviewerActivity";

    private int mGroupId;
    private String mGroupName;
    private GrupoType mGroupType;

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
    protected void initFragmentVariables(Fragment f) {
        if (!(f instanceof ReviewerFragment)) {
            return;
        }
        ReviewerFragment fragment = (ReviewerFragment) f;
        Intent intent = getIntent();
        mGroupId = intent.getIntExtra(EXTRA_GROUP_ID, -1);
        mGroupName = intent.getStringExtra(EXTRA_GROUP_NAME);
        mGroupType = GrupoType.valueOf(intent.getStringExtra(EXTRA_GROUP_TYPE));
        fragment.setGroupId(mGroupId);
        fragment.setGroupType(mGroupType);
        fragment.setGroupName(mGroupName);
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

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected FeedbackerFragment<Intent> returnFragmentType() {
        return new ReviewerFragment();
    }

}
