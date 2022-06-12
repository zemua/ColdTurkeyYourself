package devs.mrp.coolyourturkey.grupos.reviewer.tabs.addcondition;

import android.content.Intent;

import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.comun.IntentAttacher;
import devs.mrp.coolyourturkey.comun.SingleFragmentActivity;
import devs.mrp.coolyourturkey.comun.impl.IntentAttacherImpl;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoConditionRepository;
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
                if (mGroupId == -1) {
                    finish();
                    return;
                }
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
        IntentAttacher attacher = new IntentAttacherImpl(intent);
        if (!attacher.isRead()) {
            return new AddConditionFragment(mGroupId, mGroupName);
        }
        GrupoCondition condition = (GrupoCondition) attacher.read(ConditionActionConstants.EXTRA_GROUP_CONDITION, GrupoCondition.class, new GrupoCondition());
        return new AddConditionFragment(mGroupId, mGroupName, condition, true);
    }
}
