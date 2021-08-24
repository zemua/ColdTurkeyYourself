package devs.mrp.coolyourturkey.condicionesnegativas.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.condicionesnegativas.CondicionesNegativasActivity;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroupRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public class AddNegativeConditionActivity extends AppCompatActivity {

    public static final String EXTRA_CONDITION_ID = "extra_condition_id";

    public static final Integer RESULT_SAVE = 0;
    public static final Integer RESULT_DELETE = 1;

    private Fragment mFragment;
    private Integer mConditionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        Intent intent = getIntent();
        mConditionId = intent.getIntExtra(EXTRA_CONDITION_ID, -1);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = new AddNegativeConditionFragment();
            loadCondition(intent);
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        } else {
            loadCondition(intent);
        }

        ((AddNegativeConditionFragment)mFragment).addFeedbackListener(new FeedbackListener<ConditionNegativeToGroup>() {
            @Override
            public void giveFeedback(int tipo, ConditionNegativeToGroup feedback, Object... args) {
                ConditionNegativeToGroupRepository repository;
                switch (tipo) {
                    case AddNegativeConditionFragment.FEEDBACK_SAVE:
                        repository = ConditionNegativeToGroupRepository.getRepo(AddNegativeConditionActivity.this.getApplication());
                        repository.insert((ConditionNegativeToGroup) feedback);
                        setResult(RESULT_SAVE);
                        finish();
                        break;
                    case AddNegativeConditionFragment.FEEDBACK_DELETE_CONDITION:
                        repository = ConditionNegativeToGroupRepository.getRepo(AddNegativeConditionActivity.this.getApplication());
                        repository.deleteById(feedback.getId());
                        setResult(RESULT_DELETE);
                        finish();
                        break;
                }
            }
        });
    }

    private void loadCondition(Intent intent) {
        if (intent.hasExtra(CondicionesNegativasActivity.EXTRA_CONDITION)) {
            ConditionNegativeToGroup condition = (ConditionNegativeToGroup) ((ObjectWrapperForBinder)getIntent().getExtras().getBinder(CondicionesNegativasActivity.EXTRA_CONDITION)).getData();
            ((AddNegativeConditionFragment)mFragment).setConditionForEdit(condition);
        }
    }

}
