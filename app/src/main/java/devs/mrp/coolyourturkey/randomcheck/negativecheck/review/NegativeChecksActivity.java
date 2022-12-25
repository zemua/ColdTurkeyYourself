package devs.mrp.coolyourturkey.randomcheck.negativecheck.review;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Optional;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.comun.impl.TransferWithBindersImpl;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.randomcheck.negativecheck.chainfeedback.NegativeChecksFeedbackChainComander;

public class NegativeChecksActivity extends AppCompatActivity {

    public static final String KEY_FOR_RECEIVED_CHECK = "key for received check";

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null ){
            mFragment = new NegativeChecksFragment();
            addReceivedCheck((NegativeChecksFragment) mFragment);
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        } else {
            addReceivedCheck((NegativeChecksFragment) mFragment);
        }

        ((NegativeChecksFragment)mFragment).addObserver(new MyObserver<Check>() {
            @Override
            public void callback(String tipo, Check feedback) {
                new NegativeChecksFeedbackChainComander(NegativeChecksActivity.this).getHandlerChain().receiveRequest(tipo, feedback);
            }
        });
    }

    private void addReceivedCheck(NegativeChecksFragment f) {
        Optional<Object> optional = (Optional<Object>) TransferWithBindersImpl.receiveAndRead(getIntent(), KEY_FOR_RECEIVED_CHECK);
        if (optional.isPresent()) {
            f.setCheck((Check) optional.get());
        }
    }

}
