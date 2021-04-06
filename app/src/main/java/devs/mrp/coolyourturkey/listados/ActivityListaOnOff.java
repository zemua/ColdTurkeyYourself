package devs.mrp.coolyourturkey.listados;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

import java.io.Serializable;

public class ActivityListaOnOff extends AppCompatActivity implements FeedbackReceiver {
    private static final String TAG = "ACTIVITY LISTA ON OFF";

    public static final String EXTRA_TIPO_LISTA = "tipo lista";

    private String tipoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        Intent intent = getIntent();
        tipoActual = intent.getStringExtra(EXTRA_TIPO_LISTA);
        Log.d(TAG, tipoActual);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new FragmentListaOnOff();
            ((FragmentListaOnOff) fragment).setTipoActual(tipoActual);
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void receiveFeedback(Object feedbacker, int accion, Object feedback, Object ... args) {
        if (feedbacker instanceof FragmentListaOnOff) {
            switch (accion) {
                case FragmentListaOnOff.REQUEST_ACTIVAR_POSITIVA:
                    FragmentListaOnOff fragmentp = (FragmentListaOnOff)feedbacker;
                    AplicacionListada appp = (AplicacionListada) feedback;
                    Object[] objp = args;
                    Integer posicionp = (Integer)objp[0];
                    ((FragmentListaOnOff) feedbacker).muestraDialogo(getSupportFragmentManager(), FragmentListaOnOff.REQUEST_ACTIVAR_POSITIVA, appp, posicionp);
                    break;
                case FragmentListaOnOff.REQUEST_DESACTIVAR_NEGATIVA:
                    FragmentListaOnOff fragmentn = (FragmentListaOnOff)feedbacker;
                    AplicacionListada appn = (AplicacionListada) feedback;
                    Object[] objn = args;
                    Integer posicionn = (Integer)objn[0];
                    ((FragmentListaOnOff) feedbacker).muestraDialogo(getSupportFragmentManager(), FragmentListaOnOff.REQUEST_DESACTIVAR_NEGATIVA, appn, posicionn);
                    break;
            }
        }
    }

    public enum TipoLista implements Serializable {
        Positivas, Negativas;
    }

    public static Intent newIntent(Context packageContext, String tipoLista) {
        Intent intent = new Intent(packageContext, ActivityListaOnOff.class);
        intent.putExtra(EXTRA_TIPO_LISTA, tipoLista);
        return intent;
    }
}
