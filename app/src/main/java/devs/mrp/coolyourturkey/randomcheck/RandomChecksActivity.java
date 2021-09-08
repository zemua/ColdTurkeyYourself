package devs.mrp.coolyourturkey.randomcheck;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObserver;

public class RandomChecksActivity extends AppCompatActivity {

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = new RandomChecksFragment();
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        }

        ((RandomChecksFragment)mFragment).addObserver(new MyObserver<Object>() {
            @Override
            public void callback(String tipo, Object feedback) {
                new CheckChainComander().getHandlerChain().receiveRequest(tipo, RandomChecksActivity.this);
            }
        });
    }

}
