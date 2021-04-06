package devs.mrp.coolyourturkey.usagestats;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        negativeadapter = StatsAdapterDetail.createDetailedStatsAdapter(mContext);
        neutralAdapter = StatsAdapterDetail.createDetailedStatsAdapter(mContext);

        mHandler = new StatsListHandler(mContext);
        // observar la db por cambios, y si los hay notificarle al adapter
        mAplicacionViewModel = new ViewModelProvider(this, factory).get(AplicacionListadaViewModel.class);

        mAplicacionViewModel.getPositiveApps().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
            @Override
            public void onChanged(@Nullable final List<AplicacionListada> aplicacionesListadas) {
                List<AplicacionListada> apps = mHandler.quitarDesinstaladas(aplicacionesListadas);
                apps = mHandler.quitaSinTiempo(apps);
                apps = mHandler.ordenaPorTiempo(apps, mHandler.getTimeComparator());
                positiveAdapter.fitToDb(apps);
            }
        });

        mAplicacionViewModel.getNegativeApps().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
            @Override
            public void onChanged(@Nullable final List<AplicacionListada> aplicacionesListadas) {
                List<AplicacionListada> apps = mHandler.quitarDesinstaladas(aplicacionesListadas);
                apps = mHandler.quitaSinTiempo(apps);
                apps = mHandler.ordenaPorTiempo(apps, mHandler.getTimeComparator());
                negativeadapter.fitToDb(apps);
            }
        });

        mAplicacionViewModel.getmAppsPositivasNegativas().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
            @Override
            public void onChanged(List<AplicacionListada> aplicacionListadas) {
                List<AplicacionListada> app = mHandler.quitarDesinstaladas(aplicacionListadas);
                app = mHandler.dameTodasLasNeutras(app);
                app = mHandler.quitaSinTiempo(app);
                app = mHandler.ordenaPorTiempo(app, mHandler.getTimeComparator());
                neutralAdapter.fitToDb(app);
            }
        });


        positiveRecyclerView.setAdapter(positiveAdapter);
        negativeRecyclerView.setAdapter(negativeadapter);
        neutralRecyclerView.setAdapter(neutralAdapter);

        return v;
    }
}
