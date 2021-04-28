package devs.mrp.coolyourturkey.grupospositivos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.grupospositivos.conditions.AddGroupConditionActivity;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public class ExportGroupTimeActivity extends AppCompatActivity {

    private static final String TAG = "ACTIVITY_EXPORT_GROUP";

    public static final String EXTRA_GROUP_ID = "extra.group.id";
    public static final String EXTRA_GROUP_NAME = "extra.group.name";

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
            fragment = new ExportGroupTimeFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
        ((ExportGroupTimeFragment)fragment).setGroupId(mGroupId);
        ((ExportGroupTimeFragment)fragment).setGroupName(mGroupName);
        ((ExportGroupTimeFragment)fragment).addFeedbackListener(new FeedbackListener<Object>() {
            @Override
            public void giveFeedback(int tipo, Object feedback, Object... args) {

            }
        });
    }

    private void addGroupDataAsExtra(Intent intent) {
        intent.putExtra(AddGroupConditionActivity.EXTRA_GROUP_ID, mGroupId);
        intent.putExtra(AddGroupConditionActivity.EXTRA_GROUP_NAME, mGroupName);
    }

}
