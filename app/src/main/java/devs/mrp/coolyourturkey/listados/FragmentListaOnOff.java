package devs.mrp.coolyourturkey.listados;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.deprecated.apptogroup.AppToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaViewModel;
import devs.mrp.coolyourturkey.listados.callables.ListerConstructor;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FragmentListaOnOff extends Fragment {
    private static final String TAG = "fragment_lista_on_off";

    private static final String KEY_BUNDLE_TIPO_ACTUAL = "key.bundle.tipo.actual";

    private RecyclerView recyclerView;
    private AppsListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FeedbackReceiver mFeedbackReceiver;
    private Context mContext;
    private AppLister mAppLister;
    private boolean mostrandoAppsSistema = false;
    private AplicacionListadaViewModel mAplicacionViewModel;
    private AppToGroupRepository appToGroupRepository;
    private List<AplicacionListada> listaAplicaciones;
    private TextView mTextoTitulo;
    private DialogTimeUpdater mDialogTimeUpdater;

    private Handler mainHandler;

    String tipoActual; // Comparar con campo de "AplicacionListada"
    public static final String POSITIVA = AplicacionListada.POSITIVA;
    public static final String NEGATIVA = AplicacionListada.NEGATIVA;
    public static final String NEUTRAL = AplicacionListada.NEUTRAL;
    public static final int REQUEST_ACTIVAR_POSITIVA = 0;
    public static final int REQUEST_DESACTIVAR_NEGATIVA = 1;
    public static final int FEEDBACK_CONFIRMA_ACTIVAR_POSITIVA = 10;
    public static final int FEEDBACK_CONFIRMA_DESACTIVAR_NEGATIVA = 11;
    public static final String DIALOG_CONFIRMACION = "Dialogo Confirmacion";

    ViewModelProvider.Factory factory;

    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private FutureTask<AppLister> updateDatasetTask;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainHandler = new Handler(context.getMainLooper());
        //mFeedbackReceiver = (FeedbackReceiver) context;
        //mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());

        mFeedbackReceiver = (FeedbackReceiver) getActivity();
        mContext = getActivity();

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            setTipoActual(savedInstanceState.getString(KEY_BUNDLE_TIPO_ACTUAL));
        }

        View v = inflater.inflate(R.layout.fragment_lista_on_off, null);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton botonMostrar = (FloatingActionButton) v.findViewById(R.id.floatingViewButton);
        botonMostrar.hide();
        botonMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSystemVisible();
            }
        });

        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        mAdapter = new AppsListAdapter(mContext, tipoActual);
        mAdapter.resetLoaded();

        updateDatasetTask = new FutureTask<AppLister>(new ListerConstructor(mContext)) {
            @Override
            protected void done() {
                try {
                    mAppLister = get();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            mAdapter.changeToDataset(mAppLister);
                            botonMostrar.show();
                        }
                    });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        recyclerView.setAdapter(mAdapter);
        // observar la db por cambios, y si los hay notificarle al adapter
        mAplicacionViewModel = new ViewModelProvider(this, factory).get(AplicacionListadaViewModel.class);
        mAplicacionViewModel.getAllApps().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>(){
            @Override
            public void onChanged(@Nullable final List<AplicacionListada> aplicacionesListadas) {
                //Log.d(TAG, "onChanged");
                mAdapter.fitToDb(aplicacionesListadas);
                listaAplicaciones = aplicacionesListadas;
            }
        });
        mAdapter.addFeedbackListener(new FeedbackListener<AplicacionListada>(){
            @Override
            public void giveFeedback(int tipo, AplicacionListada feedback, Object... args) {
                if (tipo == AppsListAdapter.FEEDBACK_INSERT){
                    //if we are removing an app from the positive list to become "neutral"
                    if (feedback.getLista().equals(AplicacionListada.getNEUTRAL())){
                        // delete from the group in case it is assigned
                        appToGroupRepository.deleteByPackage(feedback.getNombre());
                    }
                    mAplicacionViewModel.insert(feedback); // on conflict replace
                }
                if (tipo == AppsListAdapter.FEEDBACK_REQUEST_CONFIRM) {
                    AplicacionListada app = feedback;
                    Object[] obs = args;
                    Integer posicion = (Integer)obs[0];
                    if (app.getLista().equals(AppsListAdapter.POSITIVA)) {
                        // en esta situacion se ha pulsado un switch para activar app positva
                        mFeedbackReceiver.receiveFeedback(FragmentListaOnOff.this, REQUEST_ACTIVAR_POSITIVA, app, posicion);
                    } else if (app.getLista().equals(AppsListAdapter.NEUTRAL)){
                        // en esta situación se ha pulsado un switch para desactivar app negativa
                        mFeedbackReceiver.receiveFeedback(FragmentListaOnOff.this, REQUEST_DESACTIVAR_NEGATIVA, app, posicion);
                    }
                }
            }
        });

        appToGroupRepository = AppToGroupRepository.getRepo(getActivity().getApplication());


        mTextoTitulo = (TextView) v.findViewById(R.id.text_lista_titulo);
        setTitulo(tipoActual);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        executor.execute(updateDatasetTask);
    }

    private void setTemporaryAdapter() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_BUNDLE_TIPO_ACTUAL, tipoActual);
        if (mDialogTimeUpdater != null) {
            mDialogTimeUpdater.interrumpe();
        }
        super.onSaveInstanceState(outState);
    }

    // para mostrar u ocultar las aplicaciones del sistema
    private void setSystemVisible() {
        if (!mostrandoAppsSistema){
            mAppLister.setSystemList();
            mAdapter.changeToDataset(mAppLister);
            mostrandoAppsSistema = true;
            Toast.makeText(getActivity(), R.string.toast_mostrando_apps_sistema, Toast.LENGTH_SHORT).show();
        }else{
            mAppLister.setNonSystemList();
            mAdapter.changeToDataset(mAppLister);
            mostrandoAppsSistema = false;
            Toast.makeText(getActivity(), R.string.toast_ocultando_apps_sistema, Toast.LENGTH_SHORT).show();
        }
    }

    public void setTipoActual(String s){
        tipoActual = s;
        setTitulo(s);
    }

    private void setTitulo(String s){
        if (s != null && mTextoTitulo != null && mContext != null){
            switch (s){
                case POSITIVA:
                    mTextoTitulo.setText(mContext.getString(R.string.apps_buenas));
                    mTextoTitulo.setTextColor(Color.WHITE);
                    mTextoTitulo.setBackground(mContext.getDrawable(R.drawable.green_rounded_corner_with_border));
                    mTextoTitulo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plus_circle_multiple, 0, 0, 0);
                    break;
                case NEGATIVA:
                    mTextoTitulo.setText(mContext.getString(R.string.apps_malas));
                    mTextoTitulo.setTextColor(Color.WHITE);
                    mTextoTitulo.setBackground(mContext.getDrawable(R.drawable.red_rounded_corner_with_border));
                    mTextoTitulo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bug, 0, 0, 0);
                    break;
            }
        }
    }

    public void muestraDialogo(FragmentManager fm, int tipo, AplicacionListada app, Integer posicion) {
        String mensaje = "";
        if (tipo == REQUEST_ACTIVAR_POSITIVA) {
            mensaje = this.getString(R.string.confirmacion_si_es_positiva);
        } else if (tipo == REQUEST_DESACTIVAR_NEGATIVA){
            mensaje = this.getString(R.string.confirmacion_no_es_negativa);
        }

        ApplicationInfo appinfo = null;
        try {
            appinfo = mContext.getPackageManager().getApplicationInfo(app.getNombre(), 0);
        } catch (Exception e){
            e.printStackTrace();
        }

        Drawable icon = mContext.getPackageManager().getApplicationIcon(appinfo);
        String titulo = String.valueOf(mContext.getPackageManager().getApplicationLabel(appinfo));

        WaiterConfirmDialog dialogo = new WaiterConfirmDialog(mensaje, titulo, posicion, appinfo, app.getNombre(), app.getLista());

        dialogo.setTargetFragment(FragmentListaOnOff.this, tipo);
        dialogo.show(fm, DIALOG_CONFIRMACION);

        // y devolver el switch a su posicion
        mAdapter.desSwitchear(listaAplicaciones);

        // poner cuenta atrás en el botón cuando esté lista la instancia del AlertDialog
        /*dialogo.addFeedbackListener(new FeedbackListener<AlertDialog>() {
            @Override
            public void giveFeedback(int tipo, AlertDialog feedback, Object... args) {
                if (tipo == WaiterConfirmDialog.FEEDBACK_ALERT_DIALOG) {
                    mDialogTimeUpdater = new DialogTimeUpdater(feedback, WaiterConfirmDialog.CUENTA_ATRAS_SEGUNDOS, mContext.getString(R.string.aceptar), mContext);
                    mDialogTimeUpdater.go();
                    mDialogTimeUpdater.addFeedbackListener(new FeedbackListener<Integer>() {
                        @Override
                        public void giveFeedback(int tipo, Integer feedback, Object... args) {
                            if (tipo == DialogTimeUpdater.FEEDBACK_OK) {
                                dialogo.pulsadoAceptar();
                            }
                        }
                    });
                }
            }
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Integer posicion = data.getIntExtra(WaiterConfirmDialog.EXTRA_POSICION, -1);
        String nombre = data.getStringExtra(WaiterConfirmDialog.EXTRA_NOMBRE);
        String lista = data.getStringExtra(WaiterConfirmDialog.EXTRA_LISTA);
        AplicacionListada app;
        Boolean aceptado = data.getBooleanExtra(WaiterConfirmDialog.EXTRA_RESPUESTA, false);
        if (nombre != null && nombre != "" && lista != null && lista != "") {
            app = new AplicacionListada(nombre, lista);
        } else {
            app = null;
        }

        if (resultCode != Activity.RESULT_OK || app == null) {
            if (requestCode == REQUEST_ACTIVAR_POSITIVA || requestCode == REQUEST_DESACTIVAR_NEGATIVA) {
                mAdapter.desSwitchear(listaAplicaciones);
                if (mDialogTimeUpdater != null){
                    mDialogTimeUpdater.interrumpe();
                }
            }
            return;
        }
        if (resultCode == Activity.RESULT_OK && (requestCode == REQUEST_ACTIVAR_POSITIVA || requestCode == REQUEST_DESACTIVAR_NEGATIVA) && app != null) {
            mAdapter.resetLoaded();
            mAplicacionViewModel.insert(app);
        }
    }
}
