package devs.mrp.coolyourturkey.condicionesnegativas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.DialogWithDelay;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public class CondicionesNegativasFragment extends Fragment implements MyObservable<ConditionNegativeToGroup> {

    private static String TAG_DIALOGO_CON_DELAY = "Dialogo.con.delay.condiciones.negativas.fragment.java";

    public static final String CALLBACK_ADD_CONDITION = "callback_add_condition";
    public static final String CALLBACK_EDIT_EXISTING_CONDITION = "callback_edit_existing_condition";

    private static final Integer RESULT_CONFIRM_EDIT_CONDITION = 20;

    private List<MyObserver<ConditionNegativeToGroup>> observers = new ArrayList<>();
    Map<Integer, GrupoPositivo> mMapaGrupos;
    Map<Integer, ConditionNegativeToGroup> mMapaConditions;
    private Context mContext;

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

        View v = inflater.inflate(R.layout.fragment_condiciones_negativas, container, false);

        addButton = v.findViewById(R.id.add);
        recycler = v.findViewById(R.id.recycler);

        NegativeConditionTimeChecker timeChecker = new NegativeConditionTimeChecker(mContext, this.getActivity().getApplication(), this);
        CondicionesNegativasAdapter adapter = new CondicionesNegativasAdapter(mContext, timeChecker);

        GrupoPositivoRepository gruposRepo = GrupoPositivoRepository.getRepo(requireActivity().getApplication());
        gruposRepo.findAllGrupoPositivo().observe(this, new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                mMapaGrupos = grupoPositivos.stream().collect(Collectors.toMap(GrupoPositivo::getId, g -> g));
                adapter.setGrupos(mMapaGrupos);
            }
        });

        timeChecker.addFeedbackListener(new FeedbackListener<List<ConditionNegativeToGroup>>() {
            @Override
            public void giveFeedback(int tipo, List<ConditionNegativeToGroup> feedback, Object... args) {
                switch (tipo){
                    case NegativeConditionTimeChecker.FEEDBACK_CONDITIONS_LOADED:
                        adapter.setDataset(feedback);
                        mMapaConditions = feedback.stream().collect(Collectors.toMap(ConditionNegativeToGroup::getId, c -> c));
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
                        DialogWithDelay dialog = new DialogWithDelay(R.drawable.bug, mContext.getString(R.string.apps_malas), mContext.getString(R.string.seguro_debes_modificar_esta_condicion), feedback.getId());
                        dialog.setTargetFragment(CondicionesNegativasFragment.this, RESULT_CONFIRM_EDIT_CONDITION);
                        dialog.show(getActivity().getSupportFragmentManager(), TAG_DIALOGO_CON_DELAY);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_CONFIRM_EDIT_CONDITION) {
                doCallBack(CALLBACK_EDIT_EXISTING_CONDITION, mMapaConditions.get(resultData.getIntExtra(DialogWithDelay.EXTRA_REPLY_VALUE, -1)));
            }
        }
    }

}
