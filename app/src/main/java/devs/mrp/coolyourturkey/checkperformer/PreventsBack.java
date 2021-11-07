package devs.mrp.coolyourturkey.checkperformer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.MainActivity;
import devs.mrp.coolyourturkey.R;

public class PreventsBack extends AppCompatActivity {

    public static final String INTENT_IF_PRIZE = "intent.if.prize";

    private Fragment mFragment;
    private FragmentManager fm;

    private boolean prize = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra(INTENT_IF_PRIZE, false)) {
            prize = true;
        }

        setContentView(R.layout.activity_singlefragment);

        fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = new PrizeConfirmationFragment();
            ((PrizeConfirmationFragment)mFragment).setIfPrize(prize);
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        }

        ((PrizeConfirmationFragment)mFragment).addObserver((tp, fdbck) -> {
            //finish() // not using finish because if the user opens processses and taps on this "finished" one, it is reconstructed from the beginning and starts asking the prize questions again
            Intent intent = new Intent(PreventsBack.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {}
}
