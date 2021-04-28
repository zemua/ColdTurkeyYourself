package devs.mrp.coolyourturkey.grupospositivos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.grupospositivos.conditions.AddGroupConditionActivity;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class ReviewGroupActivity extends AppCompatActivity implements FeedbackReceiver<Fragment, Object> {

    private static final String TAG = "ACTIVITY_REVIEW_GROUP";

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";
    public static final String EXTRA_CONDITION_ID = "extra_condition_id";
    public static final String EXTRA_CONDITION = "extra_condition";

    public static final String RESULT_DELETE = "result_delete_group";

    private Fragment fragment;
    private Integer mGroupId;
    private String mGroupName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        Intent intent = getIntent();
        mGroupId = intent.getIntExtra(EXTRA_GROUP_ID, -1);
        mGroupName = intent.getStringExtra(EXTRA_GROUP_NAME);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new ReviewGroupFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
        ((ReviewGroupFragment)fragment).setGroupId(mGroupId);
    }

    @Override
    public void receiveFeedback(Fragment feedbacker, int accion, Object feedback, Object... args) {
        if (feedbacker == fragment) {
            Intent intent;
            switch (accion) {
                case ReviewGroupFragment.FEEDBACK_DELETE_GROUP:
                    intent = new Intent();
                    intent.putExtra(RESULT_DELETE, mGroupId);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                case ReviewGroupFragment.FEEDBACK_ADD_CONDITION:
                    intent = new Intent(ReviewGroupActivity.this, AddGroupConditionActivity.class);
                    addGroupDataAsExtra(intent);
                    startActivity(intent);
                    break;
                case ReviewGroupFragment.FEEDBACK_CLICK_CONDITION:
                    intent = new Intent(ReviewGroupActivity.this, AddGroupConditionActivity.class);
                    addGroupDataAsExtra(intent);
                    final ConditionToGroup objSent = (ConditionToGroup)feedback;
                    final Bundle bundle = new Bundle();
                    bundle.putBinder(EXTRA_CONDITION, new ObjectWrapperForBinder(objSent));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case ReviewGroupFragment.FEEDBACK_EXPORT_TXT:
                    // TODO start new intent to setup the export
                    break;
            }
        }
    }

    private void addGroupDataAsExtra(Intent intent) {
        intent.putExtra(AddGroupConditionActivity.EXTRA_GROUP_ID, mGroupId);
        intent.putExtra(AddGroupConditionActivity.EXTRA_GROUP_NAME, mGroupName);
    }
}
