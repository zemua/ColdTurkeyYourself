package devs.mrp.coolyourturkey.randomcheck.negativecheck.lists;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.randomcheck.negativecheck.chainclick.NegativeChecksChainComander;

public class NegativeCheckListActivity extends AppCompatActivity {

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = new NegativeCheckListFragment();
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        }

        ((NegativeCheckListFragment)mFragment).addObserver(new MyObserver<Check>() {
            @Override
            public void callback(String tipo, Check feedback) {
                new NegativeChecksChainComander(NegativeCheckListActivity.this).getHandlerChain().receiveRequest(tipo, feedback);
            }
        });
    }

}
