package devs.mrp.coolyourturkey.randomcheck.timeblocks.review;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;
import java.util.Optional;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.impl.TransferWithBindersImpl;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.feedbackchain.BlockFeedbackCommander;
import devs.mrp.coolyourturkey.watchdog.checkscheduling.CheckManager;

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
            Optional<Object> opt = TransferWithBindersImpl.receiveAndRead(getIntent(), KEY_FOR_RECEIVED_TIME_BLOCK);
            if (opt.isPresent()) {
                mFragment = new TimeBlocksFragment((AbstractTimeBlock) opt.get());
            } else {
                mFragment = new TimeBlocksFragment();
            }
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        }

        ((TimeBlocksFragment)mFragment).addObserver((tipo, feedback) -> {
            CheckManager checker = CheckManager.getInstance();
            if (Objects.nonNull(checker)) {
                BlockFeedbackCommander.get(this, checker).receiveRequest(tipo, feedback);
            }
        });
    }

}
