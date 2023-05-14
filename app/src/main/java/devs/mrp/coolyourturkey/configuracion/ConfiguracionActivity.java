package devs.mrp.coolyourturkey.configuracion;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import dagger.hilt.android.AndroidEntryPoint;
import devs.mrp.coolyourturkey.R;

@AndroidEntryPoint
public class ConfiguracionActivity extends AppCompatActivity {

    private static final String TAG = "CONFIGURACION ACTIVITY";

    FragmentManager mFm;
    Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        mFm = getSupportFragmentManager();
        mFragment = mFm.findFragmentById(R.id.configuracion_fragment_container);
        if (mFragment == null){
            mFragment = new ConfiguracionFragment();
            mFm.beginTransaction().add(R.id.configuracion_fragment_container, mFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
