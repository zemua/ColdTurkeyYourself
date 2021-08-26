package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.comun.TransferWithBinders;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.chainfeedback.PositiveCheckFeedbackComander;

public class PositiveChecksActivity extends AppCompatActivity {

    public static final String KEY_FOR_RECEIVED_CHECK = "key for received check";

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = new PositiveChecksFragment();
            addReceivedCheck((PositiveChecksFragment) mFragment);
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        } else {
            addReceivedCheck((PositiveChecksFragment) mFragment);
        }

        ((PositiveChecksFragment)mFragment).addObserver(new MyObserver<PositiveCheck>() {
            @Override
            public void callback(String tipo, PositiveCheck feedback) {
                new PositiveCheckFeedbackComander(PositiveChecksActivity.this).getHandlerChain().receiveRequest(tipo, feedback);
            }
        });
    }

    private void addReceivedCheck(PositiveChecksFragment f) {
        Optional<Object> optional = (Optional<Object>)TransferWithBinders.receiveAndRead(getIntent(), KEY_FOR_RECEIVED_CHECK);
        if (optional.isPresent()) {
            f.setCheck((PositiveCheck) optional.get());
        }
    }

}
