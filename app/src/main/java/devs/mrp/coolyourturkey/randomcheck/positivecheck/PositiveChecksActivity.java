package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public class PositiveChecksActivity extends AppCompatActivity {

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = new PositiveChecksFragment();
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        }

        ((PositiveChecksFragment)mFragment).addFeedbackListener(new FeedbackListener<Object>() {
            @Override
            public void giveFeedback(int tipo, Object feedback, Object... args) {
                // TODO implement chain of responsibility
            }
        });
    }

}