package devs.mrp.coolyourturkey.grupos.reviewer.tabs.addcondition;

import android.content.Intent;
import android.os.Bundle;

import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoConditionRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public class AddConditionActivity extends SingleFragmentActivity {

    private static final String TAG = "AddConditionActivity";

    private GrupoConditionRepository mGrupoConditionRepository;
    private int mGroupId;
    private String mGroupName;

    @Override
    protected void initCallbackRegisters() {
        mGrupoConditionRepository = GrupoConditionRepository.getRepo(getApplication());
    }

    @Override
    protected void initListeners(FeedbackerFragment f) {
        f.addFeedbackListener(new FeedbackListener<GrupoCondition>() {
            @Override
            public void giveFeedback(int tipo, GrupoCondition feedback, Object... args) {
                switch (tipo) {
                    case ConditionActionConstants.ACTION_SAVE:
                        mGrupoConditionRepository.insert(feedback);
                        finish();
                        break;
                    case ConditionActionConstants.ACTION_DELETE:
                        mGrupoConditionRepository.deleteById(feedback.getId());
                        finish();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected FeedbackerFragment returnFragmentInstance() {
        Intent intent = getIntent();
        mGroupId = intent.getIntExtra(ConditionActionConstants.EXTRA_GROUP_ID, -1);
        mGroupName = intent.getStringExtra(ConditionActionConstants.EXTRA_GROUP_NAME);
        return new AddConditionFragment(mGroupId, mGroupName);
    }
}
