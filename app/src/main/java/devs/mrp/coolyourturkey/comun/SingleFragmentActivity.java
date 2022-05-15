package devs.mrp.coolyourturkey.comun;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;

public abstract class SingleFragmentActivity<T> extends AppCompatActivity {

    private Fragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = returnFragmentType();
            initFragmentVariables(fragment);
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
        initCallbackRegisters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListeners(fragment);
    }

    protected abstract void initCallbackRegisters();

    protected abstract void initFragmentVariables(Fragment f);

    protected abstract void initListeners(Fragment f);

    protected abstract String getTag();

    protected Fragment getFragment() {
        return fragment;
    }

    protected abstract FeedbackerFragment<T> returnFragmentType();

}
