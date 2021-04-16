package devs.mrp.coolyourturkey.grupospositivos;

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
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoViewModel;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class GruposPositivosFragment extends Fragment {

    private static String TAG = "GRUPOS_POSITIVOS_FRAGMENT";

    public static final int FEEDBACK_NEW_GROUP = 0;
    public static final int FEEDBACK_ITEM_CLICKED = 1;

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;
    private GrupoPositivoViewModel mGrupoPositivoViewModel;
    private ViewModelProvider.Factory factory;

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
        View v = inflater.inflate(R.layout.fragment_grupospositivos, container, false);
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());

        mAddGrupoButton = (Button) v.findViewById(R.id.button_add_group);
        mAddGrupoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(GruposPositivosFragment.this, FEEDBACK_NEW_GROUP, mFeedbackReceiver);
            }
        });

        mAdapter = new GruposPositivosAdapter(mGroupList, mContext);
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
        mGrupoPositivoViewModel.insert(grupoPositivo);
    }

    public void removeGrupoPositivoFromDb(Integer id){
        // Delete apps belonging to this group
        AppToGroupRepository repo = AppToGroupRepository.getRepo(getActivity().getApplication());
        repo.deleteByGroupId(id);
        // Delete this group
        mGrupoPositivoViewModel.deleteById(id);
    }

}
