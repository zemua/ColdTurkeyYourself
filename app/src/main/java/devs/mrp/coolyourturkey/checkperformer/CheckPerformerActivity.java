package devs.mrp.coolyourturkey.checkperformer;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservableNegative;
import devs.mrp.coolyourturkey.comun.MyObservablePositive;
import devs.mrp.coolyourturkey.comun.TransferWithBinders;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.watchdog.checkscheduling.RandomCheckWorker;

public class CheckPerformerActivity extends AppCompatActivity {

    private final String TAG = "CheckPerformerActivity";

    private Fragment mFragment;
    private AbstractTimeBlock mTimeBlock;
    private Check mNegativeCheck;
    private PositiveCheck mPositiveCheck;
    private long mPremio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        Optional<Object> optTb = TransferWithBinders.receiveAndRead(getIntent(), RandomCheckWorker.KEY_FOR_BLOCK_IN_BUNDLE);
        if (optTb.isPresent()) {
            mTimeBlock = (AbstractTimeBlock) optTb.get();
        } else {
            Log.d(TAG, "No data in the bundle!");
        }

        if (mTimeBlock.getNegativeChecks() != null && mTimeBlock.getNegativeChecks().size() > 0) {
            mNegativeCheck = mTimeBlock.getNegativeChecks().get((int)Math.random()*mTimeBlock.getNegativeChecks().size());
        }
        if (mTimeBlock.getPositiveChecks() != null && mTimeBlock.getPositiveChecks().size() > 0) {
            mPositiveCheck = mTimeBlock.getPositiveChecks().get((int)Math.random()*mTimeBlock.getPositiveChecks().size());
        }

        if (mPositiveCheck == null) {
            this.finish();
        }

        mPremio = (mTimeBlock.getMinimumLapse() + ((mTimeBlock.getMaximumLapse()-mTimeBlock.getMinimumLapse())/2)) * mPositiveCheck.getMultiplicador();

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            if (mNegativeCheck != null) {
                mFragment = new CheckPerformerFragment(mNegativeCheck.getQuestion(), true, false, false);
            } else {
                mFragment = new CheckPerformerFragment(mPositiveCheck.getQuestion(), false, true, true);
            }
        }
        fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();

        ((MyObservableNegative<Boolean>)mFragment).addNegativeObserver((tipo, bool) -> {
            if (!bool) {
                fm.beginTransaction().remove(mFragment).commit();
                mFragment = new CheckPerformerFragment(mPositiveCheck.getQuestion(), false, true, true);
                fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
            } else {
                CheckPerformerActivity.this.finish();
            }
        });

        ((MyObservablePositive<Boolean>)mFragment).addPositiveObserver((tipo, bool) -> {
            if (bool) {
                // TODO sum points, clicked yes on positive question
            } else {
                CheckPerformerActivity.this.finish();
            }
        });
    }

}
