package devs.mrp.coolyourturkey.grupospositivos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.nio.charset.Charset;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class AddGroupActivity extends AppCompatActivity implements FeedbackReceiver<Fragment, Object> {

    public static final String EXTRA_NAME = "extra_name";
    Fragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new AddGroupFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void receiveFeedback(Fragment feedbacker, int accion, Object feedback, Object... args) {
        if (feedbacker == fragment) {
            Intent intent;
            switch (accion) {
                case AddGroupFragment.FEEDBACK_NAMED:
                    intent = new Intent();
                    intent.putExtra(EXTRA_NAME, feedback.toString());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
