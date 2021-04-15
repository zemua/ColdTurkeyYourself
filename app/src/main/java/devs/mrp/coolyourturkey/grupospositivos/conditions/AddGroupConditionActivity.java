package devs.mrp.coolyourturkey.grupospositivos.conditions;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroupRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class AddGroupConditionActivity extends AppCompatActivity implements FeedbackReceiver<Fragment, Object> {

    public static final String EXTRA_GROUP_ID = "extra_group_id";

    public static final Integer RESULT_SAVE = 0;

    private Fragment fragment;
    private Integer mGroupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        Intent intent = getIntent();
        mGroupId = intent.getIntExtra(EXTRA_GROUP_ID, -1);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new AddGroupConditionFragment(mGroupId);
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void receiveFeedback(Fragment feedbacker, int accion, Object feedback, Object... args) {
        if (feedbacker == fragment) {
            Intent intent;
            switch (accion) {
                case AddGroupConditionFragment.FEEDBACK_SAVE:
                    ConditionToGroupRepository repository = ConditionToGroupRepository.getRepo(this.getApplication());
                    repository.insert((ConditionToGroup)feedback);
                    setResult(RESULT_SAVE);
                    finish();
                    break;
            }
        }
    }
}
