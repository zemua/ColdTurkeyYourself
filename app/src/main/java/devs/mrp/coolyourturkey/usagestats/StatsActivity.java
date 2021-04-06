package devs.mrp.coolyourturkey.usagestats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.PermisosChecker;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class StatsActivity extends AppCompatActivity implements FeedbackReceiver<Fragment, Object> {

    Fragment fragment;
    public static final String EXTRA_TIPO_LISTA = "tipo lista";
    public static final String EXTRA_NIVEL_DETALLE = "nivel detalle";
    public static final String EXTRA_NOMBRE_PAQUETE = "nombre paquete";

    public static final int TIPO_LISTA_POSITIVA = 0;
    public static final int TIPO_LISTA_NEGATIVA = 1;
    public static final int TIPO_LISTA_AMBAS = 2;
    public static final int DETALLE_DETALLADO = 0;
    public static final int DETALLE_GENERAL = 1;

    private static final String TAG = "STATS_ACTIVITY";

    private int tipoActual;
    private int detalleActual;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        if (PermisosChecker.checkPermisoEstadisticas(this)) {

            Intent intent = getIntent();
            tipoActual = intent.getIntExtra(EXTRA_TIPO_LISTA, 1);
            //Log.d(TAG, "tipo actual: " + tipoActual);
            detalleActual = intent.getIntExtra(EXTRA_NIVEL_DETALLE, 1);

            FragmentManager fm = getSupportFragmentManager();
            fragment = fm.findFragmentById(R.id.fragment_container);
            if (fragment == null) {
                /* int ltipo;
                if (tipoActual == TIPO_LISTA_POSITIVA) {
                    ltipo = StatsFragment.TIPO_POSITIVO;
                    fragment = new StatsFragment(ltipo);
                } else if (tipoActual == TIPO_LISTA_NEGATIVA) {
                    ltipo = StatsFragment.TIPO_NEGATIVO;
                    fragment = new StatsFragment(ltipo);
                } else {
                    ltipo = StatsFragment.TIPO_AMBOS;
                    fragment = new StatsFragmentDoble();
                } */

                fragment = new StatsFragmentDoble();

                fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
            }
        }
    }

    @Override
    public void receiveFeedback(Fragment feedbacker, int accion, Object feedback, Object ... args) {
        if (feedbacker.getClass() == StatsFragment.class) {
            switch (accion) {
                case StatsFragment.ACCION_CLICK:
                    nuevaDetalleStatsActivity(StatsActivity.this, (String) feedback);
                    break;
            }
        }
    }

    private void nuevaDetalleStatsActivity(Context packageContext, String paquete) {
        Intent intent = StatsActivity.newIntent(packageContext, tipoActual, DETALLE_DETALLADO, paquete);
        startActivity(intent);
    }

    public static Intent newIntent(Context packageContext, int tipoLista, int detalle) {
        Intent intent = new Intent(packageContext, StatsActivity.class);
        intent.putExtra(EXTRA_TIPO_LISTA, tipoLista);
        intent.putExtra(EXTRA_NIVEL_DETALLE, detalle);
        return intent;
    }

    public static Intent newIntent(Context packageContext, int tipoLista, int detalle, String nombrePaquete) {
        Intent intent = new Intent(packageContext, StatsActivity.class);
        intent.putExtra(EXTRA_TIPO_LISTA, tipoLista);
        intent.putExtra(EXTRA_NIVEL_DETALLE, detalle);
        intent.putExtra(EXTRA_NOMBRE_PAQUETE, nombrePaquete);
        return intent;
    }
}
