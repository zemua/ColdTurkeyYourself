package devs.mrp.coolyourturkey.grupospositivos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class ReviewGroupActivity extends AppCompatActivity implements FeedbackReceiver<Fragment, Object> {

    private static final String TAG = "ACTIVITY_REVIEW_GROUP";

    public static final String EXTRA_GROUP_ID = "extra_group_id";

    private Fragment fragment;
    private Integer mGroupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        Intent intent = getIntent();
        mGroupId = intent.getIntExtra(EXTRA_GROUP_ID, -1);
        Log.d(TAG, "id of the actual group: " + mGroupId);

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
                case 0:
                    break;
            }
        }
    }
}
