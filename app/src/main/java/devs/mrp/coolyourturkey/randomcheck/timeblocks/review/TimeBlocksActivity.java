package devs.mrp.coolyourturkey.randomcheck.timeblocks.review;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;

public class TimeBlocksActivity extends AppCompatActivity {

    public static final String KEY_FOR_RECEIVED_TIME_BLOCK = "key for received time block";

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = new TimeBlocksFragment();

        }
    }

}
