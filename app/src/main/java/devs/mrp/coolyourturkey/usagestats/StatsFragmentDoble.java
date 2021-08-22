package devs.mrp.coolyourturkey.usagestats;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaViewModel;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class StatsFragmentDoble extends Fragment {

    public static final int TIPO_POSITIVO = 0;
    public static final int TIPO_NEGATIVO = 1;
    public static final int TIPO_AMBOS = 2;
    public static final int ACCION_CLICK = 0;

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;
    private RecyclerView positiveRecyclerView;
    private RecyclerView negativeRecyclerView;
    private RecyclerView neutralRecyclerView;
    private RecyclerView.LayoutManager positiveLayoutManager;
    private RecyclerView.LayoutManager negativeLayoutManager;
    private RecyclerView.LayoutManager neutralLayoutManager;
    private AplicacionListadaViewModel mAplicacionViewModel;

    private Handler mainHandler;

    private StatsListHandler mHandler;

    ViewModelProvider.Factory factory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        mFeedbackReceiver = (FeedbackReceiver) context;
        mainHandler = new Handler(context.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        View v = inflater.inflate(R.layout.fragment_stats_ambos, container, false);

        positiveRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_tiempo_positivas);
        negativeRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_tiempo_negativas);
        neutralRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_tiempo_neutrales);
        positiveLayoutManager = new LinearLayoutManager(mContext);
        negativeLayoutManager = new LinearLayoutManager(mContext);
        neutralLayoutManager = new LinearLayoutManager(mContext);
        positiveRecyclerView.setLayoutManager(positiveLayoutManager);
        negativeRecyclerView.setLayoutManager(negativeLayoutManager);
        neutralRecyclerView.setLayoutManager(neutralLayoutManager);

        StatsAdapterDetail positiveAdapter;
        StatsAdapterDetail negativeadapter;
        StatsAdapterDetail neutralAdapter;

        positiveAdapter = StatsAdapterDetail.createDetailedStatsAdapter(mContext);
        ProgressBar spinnerPos = (ProgressBar) v.findViewById(R.id.spinner1);

        negativeadapter = StatsAdapterDetail.createDetailedStatsAdapter(mContext);
        ProgressBar spinnerNeg = (ProgressBar) v.findViewById(R.id.spinner2);

        neutralAdapter = StatsAdapterDetail.createDetailedStatsAdapter(mContext);
        ProgressBar spinnerNeut = (ProgressBar) v.findViewById(R.id.spinner3);

        mHandler = new StatsListHandler(mContext);
        // observar la db por cambios, y si los hay notificarle al adapter
        mAplicacionViewModel = new ViewModelProvider(this, factory).get(AplicacionListadaViewModel.class);

        // POSITIVE ADAPTER
        ExecutorService servicio = Executors.newFixedThreadPool(3);
        FutureTask<StatsAdapterDetail> positivetask = new FutureTask<StatsAdapterDetail>(new Callable<StatsAdapterDetail>() {
            @Override
            public StatsAdapterDetail call() throws Exception {
                return positiveAdapter.inicializaInstalledList(mContext);
            }
        }){
            @Override
            public void done() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        positiveAdapter.notifyDataSetChanged();
                        mAplicacionViewModel.getPositiveApps().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
                            @Override
                            public void onChanged(@Nullable final List<AplicacionListada> aplicacionesListadas) {
                                spinnerPos.setVisibility(View.GONE);
                                List<AplicacionListada> apps = mHandler.quitarDesinstaladas(aplicacionesListadas);
                                apps = mHandler.quitaSinTiempo(apps);
                                apps = mHandler.ordenaPorTiempo(apps, mHandler.getTimeComparator());
                                positiveAdapter.fitToDb(apps);
                            }
                        });
                    }
                });
            }
        };

        // NEGATIVE ADAPTER
        FutureTask<StatsAdapterDetail> negativetask = new FutureTask<StatsAdapterDetail>(new Callable<StatsAdapterDetail>() {
            @Override
            public StatsAdapterDetail call() throws Exception {
                return negativeadapter.inicializaInstalledList(mContext);
            }
        }){
            @Override
            public void done() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        negativeadapter.notifyDataSetChanged();
                        mAplicacionViewModel.getNegativeApps().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
                            @Override
                            public void onChanged(@Nullable final List<AplicacionListada> aplicacionesListadas) {
                                spinnerNeg.setVisibility(View.GONE);
                                List<AplicacionListada> apps = mHandler.quitarDesinstaladas(aplicacionesListadas);
                                apps = mHandler.quitaSinTiempo(apps);
                                apps = mHandler.ordenaPorTiempo(apps, mHandler.getTimeComparator());
                                negativeadapter.fitToDb(apps);
                            }
                        });
                    }
                });
            }
        };

        // NEUTRAL ADAPTER
        FutureTask<StatsAdapterDetail> neutraltask = new FutureTask<StatsAdapterDetail>(new Callable<StatsAdapterDetail>() {
            @Override
            public StatsAdapterDetail call() throws Exception {
                return neutralAdapter.inicializaInstalledList(mContext);
            }
        }){
            @Override
            public void done() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        neutralAdapter.notifyDataSetChanged();
                        mAplicacionViewModel.getmAppsPositivasNegativas().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
                            @Override
                            public void onChanged(List<AplicacionListada> aplicacionListadas) {
                                spinnerNeut.setVisibility(View.GONE);
                                List<AplicacionListada> app = mHandler.quitarDesinstaladas(aplicacionListadas);
                                app = mHandler.dameTodasLasNeutras(app);
                                app = mHandler.quitaSinTiempo(app);
                                app = mHandler.ordenaPorTiempo(app, mHandler.getTimeComparator());
                                neutralAdapter.fitToDb(app);
                            }
                        });
                    }
                });
            }
        };

        servicio.execute(positivetask);
        servicio.execute(negativetask);
        servicio.execute(neutraltask);

        positiveRecyclerView.setAdapter(positiveAdapter);
        negativeRecyclerView.setAdapter(negativeadapter);
        neutralRecyclerView.setAdapter(neutralAdapter);

        return v;
    }
}
