package devs.mrp.coolyourturkey;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.comun.PermisosChecker;
import devs.mrp.coolyourturkey.configuracion.ConfiguracionActivity;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.grupospositivos.GruposPositivosActivity;
import devs.mrp.coolyourturkey.listados.ActivityListaOnOff;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;
import devs.mrp.coolyourturkey.usagestats.StatsActivity;

public class MainActivity extends AppCompatActivity implements FeedbackReceiver<Fragment, Object> {

    // TODO load list of apps in different thread to not affect UI performance
    // TODO random check on questions for positive reinforcement
    // TODO points on steps/walking like a pedometer

    private static final String TAG = "Main_Activity";

    Fragment fragment;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.main_fragment_container);
        if (fragment == null) {
            fragment = new MainFragment();
            fm.beginTransaction().add(R.id.main_fragment_container, fragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPermisoEstadisticas(this)) {
            ((MainFragment) fragment).muestraDialogoPermisos(fm, MainFragment.REQUEST_PERMISO_USO);
        } else if (!PermisosChecker.checkPermisoAlertas(this)) {
            ((MainFragment) fragment).muestraDialogoPermisos(fm, MainFragment.REQUEST_PERMISO_ALERTA);
        }
    }

    @Override
    public void receiveFeedback(Fragment mfragment, int tipo, Object feedback, Object ... args) {
        if (mfragment == fragment) {
            switch (tipo) {
                case MainFragment.FEEDBACK_POSITIVAS:
                    nuevaListaAppsActivity(ActivityListaOnOff.TipoLista.Positivas);
                    break;
                case MainFragment.FEEDBACK_NEGATIVAS:
                    nuevaListaAppsActivity(ActivityListaOnOff.TipoLista.Negativas);
                    break;
                case MainFragment.FEEDBACK_TIEMPO_POSITIVO:
                    nuevaStatsActivity(StatsActivity.TIPO_LISTA_POSITIVA);
                    break;
                case MainFragment.FEEDBACK_TIEMPO_NEGATIVO:
                    nuevaStatsActivity(StatsActivity.TIPO_LISTA_NEGATIVA);
                    break;
                case MainFragment.FEEDBACK_REQ_PERMISO_USAGE:
                    if (feedback instanceof Boolean && feedback.equals(true)) {
                        requestPermisoEstadisticas(MainActivity.this);
                    }
                    break;
                case MainFragment.FEEDBACK_REQ_PERMISO_ALERTA:
                    if (feedback instanceof Boolean && feedback.equals(true)){
                        requestPermisoAlertas(MainActivity.this);
                    }
                    break;
                case MainFragment.FEEDBACK_TO_CONFIG:
                    Intent intent = new Intent(this, ConfiguracionActivity.class);
                    startActivity(intent);
                    break;
                case MainFragment.FEEDBACK_TIEMPO_DOBLE:
                    nuevaStatsActivity(StatsActivity.TIPO_LISTA_AMBAS);
                    break;
                case MainFragment.FEEDBACK_GRUPOS_POSITIVOS:
                    Intent intento = new Intent(MainActivity.this, GruposPositivosActivity.class);
                    startActivity(intento);
                    break;
            }
        }
    }

    private void nuevaListaAppsActivity(ActivityListaOnOff.TipoLista tipoLista) {
        String tipo;
        switch (tipoLista) {
            case Positivas:
                tipo = AplicacionListada.POSITIVA;
                break;
            default:
                tipo = AplicacionListada.NEGATIVA;
                break;
        }
        Intent intent = ActivityListaOnOff.newIntent(MainActivity.this, tipo);
        startActivity(intent);
    }

    private void nuevaStatsActivity(int tipoLista) {
        Intent intent = StatsActivity.newIntent(MainActivity.this, tipoLista, StatsActivity.DETALLE_GENERAL);
        startActivity(intent);
    }

    /**
     * Checar y trasladar la necesidad de permisos
     */

    public static boolean checkPermisoEstadisticas(Context contexto) {
        return PermisosChecker.checkPermisoEstadisticas(contexto);
    }

    public static boolean checkShouldShowRationale(Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad, Manifest.permission.READ_CONTACTS)) {
            return true;
        }
        return false;
    }

    public static void requestPermisoEstadisticas(Context contexto) {
        PermisosChecker.requestPermisoEstadisticas(contexto);
    }

    public static void requestPermisoAlertas(Context contexto) {
        PermisosChecker.requestPermisoAlertas(contexto);
    }
}