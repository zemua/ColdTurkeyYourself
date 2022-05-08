package devs.mrp.coolyourturkey.grupos.grupospositivos;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoViewModel;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class GruposPositivosFragment extends Fragment {

    private static String TAG = "GRUPOS_POSITIVOS_FRAGMENT";

    public static final int FEEDBACK_NEW_GROUP = 0;
    public static final int FEEDBACK_ITEM_CLICKED = 1;

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;
    private GrupoPositivoViewModel mGrupoPositivoViewModel;
    private ViewModelProvider.Factory factory;
    private TimeLogHandler mTimeLogHandler;

    private Button mAddGrupoButton;
    private RecyclerView mGroupsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GruposPositivosAdapter mAdapter;
    private List<GrupoPositivo> mGroupList = new ArrayList<>();

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
        View v = inflater.inflate(R.layout.fragment_grupos, container, false);
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        mTimeLogHandler = new TimeLogHandler(mContext, getActivity().getApplication(), getViewLifecycleOwner());

        mAddGrupoButton = (Button) v.findViewById(R.id.button_add_group);
        mAddGrupoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(GruposPositivosFragment.this, FEEDBACK_NEW_GROUP, mFeedbackReceiver);
            }
        });

        mAdapter = new GruposPositivosAdapter(mGroupList, mContext, mTimeLogHandler);
        mGroupsRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_groups);
        layoutManager = new LinearLayoutManager(mContext);
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mGroupsRecyclerView.setAdapter(mAdapter);

        mAdapter.addFeedbackListener(new FeedbackListener<GrupoPositivo>() {
            @Override
            public void giveFeedback(int tipo, GrupoPositivo feedback, Object... args) {
                if (tipo == GruposPositivosAdapter.FEEDBACK_ITEM_CLICKED) {
                    mFeedbackReceiver.receiveFeedback(GruposPositivosFragment.this, FEEDBACK_ITEM_CLICKED, feedback);
                }
            }
        });
        mTimeLogHandler.addFeedbackListener(new FeedbackListener<Object>() {
            @Override
            public void giveFeedback(int tipo, Object feedback, Object... args) {
                if (tipo == TimeLogHandler.FEEDBACK_LOGGERS_CHANGED) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mGrupoPositivoViewModel = new ViewModelProvider(this, factory).get(GrupoPositivoViewModel.class);
        mGrupoPositivoViewModel.getAllGrupos().observe(getViewLifecycleOwner(), new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                mGroupList = grupoPositivos;
                mAdapter.updateDataset(mGroupList);
                //Log.d(TAG, "Size of group db entries: " + String.valueOf(mGroupList.size()));
            }
        });

        return v;
    }

    public void addGrupoPositivoToDb(GrupoPositivo grupoPositivo) {
        Log.d(TAG, "to add group " + grupoPositivo.getId() + " " + grupoPositivo.getNombre());
        mGrupoPositivoViewModel.insert(grupoPositivo);
    }

    public void removeGrupoPositivoFromDb(Integer id){
        // Delete apps belonging to this group
        Log.d(TAG, "to start delete");
        AppToGroupRepository appRepo = AppToGroupRepository.getRepo(getActivity().getApplication());
        appRepo.deleteByGroupId(id);
        Log.d(TAG, "deleted reference of apps to this group");
        // Delete conditions from this group
        ConditionToGroupRepository conditionRepo = ConditionToGroupRepository.getRepo(getActivity().getApplication());
        conditionRepo.deleteByGroupId(id);
        Log.d(TAG, "deleted conditions of this group");
        // Delete conditions that refer to this group
        conditionRepo.deleteByConditionalGroupId(id);
        Log.d(TAG, "deleted conditions that refer to this group");
        // Delete this group
        GrupoPositivoRepository grupoRepo = GrupoPositivoRepository.getRepo(getActivity().getApplication());
        grupoRepo.deleteById(id);
        Log.d(TAG, "deleted the group");
    }

}
