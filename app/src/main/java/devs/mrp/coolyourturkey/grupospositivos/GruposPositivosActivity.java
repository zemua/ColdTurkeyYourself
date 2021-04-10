package devs.mrp.coolyourturkey.grupospositivos;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;
import devs.mrp.coolyourturkey.usagestats.StatsFragmentDoble;

public class GruposPositivosActivity extends AppCompatActivity implements FeedbackReceiver<Fragment, Object> {

    // TODO edit existing groups
    // TODO check all groups related activities that work well when turning the screen around

    private static final int LAUNCH_ADD = 1;
    private static final int LAUNCH_REVIEW = 2;

    private Fragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlefragment);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new GruposPositivosFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void receiveFeedback(Fragment feedbacker, int accion, Object feedback, Object... args) {
        if (feedbacker == fragment) {
            Intent intent;
            switch (accion) {
                case GruposPositivosFragment.FEEDBACK_NEW_GROUP:
                    intent = new Intent(GruposPositivosActivity.this, AddGroupActivity.class);
                    startActivityForResult(intent, LAUNCH_ADD);
                    break;
                case GruposPositivosFragment.FEEDBACK_ITEM_CLICKED:
                    intent = new Intent(GruposPositivosActivity.this, ReviewGroupActivity.class);
                    intent.putExtra(ReviewGroupActivity.EXTRA_GROUP_ID, (Integer)feedback);
                    startActivityForResult(intent, LAUNCH_REVIEW);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_ADD) {
            if (resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra(AddGroupActivity.EXTRA_NAME);
                GrupoPositivo g = new GrupoPositivo(result);
                ((GruposPositivosFragment)fragment).addGrupoPositivoToDb(g);
            }
        }
    }
}
