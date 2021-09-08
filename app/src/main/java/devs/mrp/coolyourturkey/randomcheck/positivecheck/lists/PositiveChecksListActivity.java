package devs.mrp.coolyourturkey.randomcheck.positivecheck.lists;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.chainclick.PositiveChecksChainComander;

public class PositiveChecksListActivity extends AppCompatActivity {

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = new PositiveChecksListFragment();
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        }

        ((PositiveChecksListFragment)mFragment).addObserver(new MyObserver<PositiveCheck>() {
            @Override
            public void callback(String tipo, PositiveCheck feedback) {
                new PositiveChecksChainComander().getHandlerChain().receiveRequest(tipo, new ContextAndCheckFacade(PositiveChecksListActivity.this, feedback));
            }
        });
    }

}
