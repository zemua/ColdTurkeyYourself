package devs.mrp.coolyourturkey.grupos.grupospositivos.conditions;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;

public class GroupLimitsActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";

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
            fragment = new GroupLimitsFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
        ((GroupLimitsFragment)fragment).setGroupId(mGroupId);
        ((GroupLimitsFragment)fragment).setGroupName(mGroupName);
    }



}
