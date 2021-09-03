package devs.mrp.coolyourturkey.checkperformer;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;
import java.util.Random;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservableNegative;
import devs.mrp.coolyourturkey.comun.MyObservablePositive;
import devs.mrp.coolyourturkey.comun.TransferWithBinders;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.watchdog.TimePusherFactory;
import devs.mrp.coolyourturkey.watchdog.TimePusherInterface;
import devs.mrp.coolyourturkey.watchdog.checkscheduling.RandomCheckWorker;

public class CheckPerformerActivity extends AppCompatActivity {

    private final String TAG = "CheckPerformerActivity";

    private Fragment mFragment;
    private TimePusherInterface timePusher;
    private long mPremio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        timePusher = new TimePusherFactory().get(ContadorRepository.getRepo(getApplication()));

        String positiveQuestion = "";
        if (getIntent().hasExtra(RandomCheckWorker.KEY_FOR_POSITIVE_QUESTION)) {
            positiveQuestion = getIntent().getStringExtra(RandomCheckWorker.KEY_FOR_POSITIVE_QUESTION);
        }
        String negativeQuestion = "";
        if (getIntent().hasExtra(RandomCheckWorker.KEY_FOR_NEGATIVE_QUESTION)) {
            negativeQuestion = getIntent().getStringExtra(RandomCheckWorker.KEY_FOR_NEGATIVE_QUESTION);
        }

        if (getIntent().hasExtra(RandomCheckWorker.KEY_FOR_PREMIO)){
            mPremio = getIntent().getLongExtra(RandomCheckWorker.KEY_FOR_PREMIO, 0L);
            Log.d(TAG, "premio: " + mPremio + " en h:m:s " + mPremio/(60*60*1000) + ":" + (mPremio%(60*60*1000))/(60*1000) + ":" + (mPremio%(60*1000)/1000));
        }
        Integer blockId;
        if (getIntent().hasExtra(RandomCheckWorker.KEY_FOR_BLOCK_ID)) {
            blockId = getIntent().getIntExtra(RandomCheckWorker.KEY_FOR_BLOCK_ID, -1);
        }

        if (positiveQuestion.equals("")) {
            finish();
        }

        final String pq = positiveQuestion;

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            if (!negativeQuestion.equals("")) {
                mFragment = new CheckPerformerFragment(negativeQuestion, true, false, false, this);
            } else {
                mFragment = new CheckPerformerFragment(positiveQuestion, false, true, true, this);
            }
        }
        fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();

        ((MyObservableNegative<Boolean>)mFragment).addNegativeObserver((tipo, bool) -> {
            if (!bool) {
                fm.beginTransaction().remove(mFragment).commit();
                mFragment = new CheckPerformerFragment(pq, false, true, true, this);
                addPositiveObserver();
                ((MyObservableNegative<?>) mFragment).addNegativeObserver((t, b) -> CheckPerformerActivity.this.finish());
                fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
            } else {
                CheckPerformerActivity.this.finish();
            }
        });

        addPositiveObserver();

    }

    private void addPositiveObserver() {
        ((MyObservablePositive<Boolean>)mFragment).addPositiveObserver((tipo, bool) -> {
            if (bool) {
                Log.d(TAG, "to add premio " + mPremio + " en h:m:s " + mPremio/(60*60*1000) + ":" + (mPremio%(60*60*1000))/(60*1000) + ":" + (mPremio%(60*1000)/1000) );
                timePusher.add(System.currentTimeMillis(), mPremio, this);
                // TODO add log of time for conditions
                CheckPerformerActivity.this.finish();
            } else {
                CheckPerformerActivity.this.finish();
            }
        });
    }

}
