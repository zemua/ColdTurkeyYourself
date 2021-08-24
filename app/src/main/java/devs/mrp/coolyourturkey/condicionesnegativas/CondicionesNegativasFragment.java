package devs.mrp.coolyourturkey.condicionesnegativas;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class CondicionesNegativasFragment extends Fragment implements MyObservable<ConditionNegativeToGroup> {

    public static final String CALLBACK_ADD_CONDITION = "callback_add_condition";
    public static final String CALLBACK_EDIT_EXISTING_CONDITION = "callback_edit_existing_condition";

    private List<MyObserver<ConditionNegativeToGroup>> observers = new ArrayList<>();
    private Context mContext;
    private ViewModelProvider.Factory factory;
    private Handler mainHandler;

    private FloatingActionButton addButton;
    private RecyclerView recycler;

    @Override
    public void addObserver(MyObserver<ConditionNegativeToGroup> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, ConditionNegativeToGroup feedback) {
        observers.stream().forEach(o -> {
            o.callback(tipo, feedback);
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        mainHandler = new Handler(mContext.getMainLooper());

        View v = inflater.inflate(R.layout.fragment_condiciones_negativas, container, false);

        addButton = v.findViewById(R.id.addNegCond);
        recycler = v.findViewById(R.id.recyclerNegCond);

        // TODO when deleting a positive group, delete all negative conditions depending on it

        NegativeConditionTimeChecker timeChecker = new NegativeConditionTimeChecker(mContext, this.getActivity().getApplication(), this);
        CondicionesNegativasAdapter adapter = new CondicionesNegativasAdapter(mContext, timeChecker);

        GrupoPositivoRepository gruposRepo = GrupoPositivoRepository.getRepo(requireActivity().getApplication());
        gruposRepo.findAllGrupoPositivo().observe(this, new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                Map<Integer, GrupoPositivo> mapaGrupos = grupoPositivos.stream().collect(Collectors.toMap(GrupoPositivo::getId, g -> g));
                adapter.setGrupos(mapaGrupos);
            }
        });

        timeChecker.addFeedbackListener(new FeedbackListener<List<ConditionNegativeToGroup>>() {
            @Override
            public void giveFeedback(int tipo, List<ConditionNegativeToGroup> feedback, Object... args) {
                switch (tipo){
                    case NegativeConditionTimeChecker.FEEDBACK_CONDITIONS_LOADED:
                        adapter.setDataset(feedback);
                        break;
                    case NegativeConditionTimeChecker.FEEDBACK_TIMES_LOADED:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        adapter.addFeedbackListener(new FeedbackListener<ConditionNegativeToGroup>() {
            @Override
            public void giveFeedback(int tipo, ConditionNegativeToGroup feedback, Object... args) {
                switch (tipo) {
                    case CondicionesNegativasAdapter.FEEDBACK_CONDITION_SELECTED:
                        doCallBack(CALLBACK_EDIT_EXISTING_CONDITION, feedback);
                        break;
                }
            }
        });

        recycler.setAdapter(adapter);
        LinearLayoutManager layout = new LinearLayoutManager(mContext);
        recycler.setLayoutManager(layout);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCallBack(CALLBACK_ADD_CONDITION, null);
            }
        });

        return v;
    }

}
