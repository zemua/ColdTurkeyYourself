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

public class StatsFragment extends Fragment {

    private static final String TAG = "STATS_FRAGMENT";

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AplicacionListadaViewModel mAplicacionViewModel;

    public static final int TIPO_POSITIVO = 0;
    public static final int TIPO_NEGATIVO = 1;
    public static final int TIPO_AMBOS = 2;
    public static final int ACCION_CLICK = 0;
    private int tipoActual;

    ViewModelProvider.Factory factory;

    public StatsFragment(int tipo) {
        super();
        tipoActual = tipo;
    }

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
        View v = inflater.inflate(R.layout.fragment_stats, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);

        StatsAdapterDetail adapter;

        adapter = StatsAdapterDetail.createDetailedStatsAdapter(mContext);

        // observar la db por cambios, y si los hay notificarle al adapter
        mAplicacionViewModel = new ViewModelProvider(this, factory).get(AplicacionListadaViewModel.class);
        if (tipoActual == TIPO_POSITIVO) {
            mAplicacionViewModel.getPositiveApps().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
                @Override
                public void onChanged(@Nullable final List<AplicacionListada> aplicacionesListadas) {
                    List<AplicacionListada> apps = new StatsListHandler(mContext).quitarDesinstaladas(aplicacionesListadas);
                    adapter.fitToDb(apps);
                }
            });
        } else if (tipoActual == TIPO_NEGATIVO) {
            mAplicacionViewModel.getNegativeApps().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
                @Override
                public void onChanged(@Nullable final List<AplicacionListada> aplicacionesListadas) {
                    List<AplicacionListada> apps = new StatsListHandler(mContext).quitarDesinstaladas(aplicacionesListadas);
                    adapter.fitToDb(apps);
                }
            });
        }

        recyclerView.setAdapter(adapter);

        return v;
    }
}
