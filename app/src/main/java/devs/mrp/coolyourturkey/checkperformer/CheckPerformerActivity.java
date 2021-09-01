package devs.mrp.coolyourturkey.checkperformer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservableNegative;
import devs.mrp.coolyourturkey.comun.MyObservablePositive;

public class CheckPerformerActivity extends AppCompatActivity {

    private Fragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            // TODO add question
            mFragment = new CheckPerformerFragment("", true, false, false);
        }
        fm.beginTransaction().add(R.id.fragment_container, mFragment).commit();

        ((MyObservableNegative<Boolean>)mFragment).addNegativeObserver((tipo, bool) -> {
            if (!bool) {
                fm.beginTransaction().remove(mFragment).commit();
                // TODO add question
                mFragment = new CheckPerformerFragment("", false, true, true);
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
