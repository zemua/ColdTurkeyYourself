package devs.mrp.coolyourturkey.randomcheck.timeblocks.lists;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlock;

public class CheckTimeBlockListActivity extends AppCompatActivity {

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = new CheckTimeBlockListFragment();
            fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();
        }

        ((CheckTimeBlockListFragment)mFragment).addObserver(new MyObserver<CheckTimeBlock>() {
            @Override
            public void callback(String tipo, CheckTimeBlock feedback) {
                // TODO make chain of responsibility
            }
        });
    }

}
