package devs.mrp.coolyourturkey.grupos;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.databaseroom.gruponegativo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.gruponegativo.GrupoViewModel;
import devs.mrp.coolyourturkey.grupos.grupospositivos.AddGroupActivity;
import devs.mrp.coolyourturkey.grupos.grupospositivos.ReviewGroupActivity;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public abstract class GruposFragment<T extends Grupo> extends FeedbackerFragment<Intent> {

    public static final int LAUNCH_INTENT = 0;

    private ViewModelProvider.Factory viewModelFactory;
    private TimeLogHandler mTimeLogHandler;
    private GruposAdapter mAdapter;
    private GrupoViewModel mGrupoViewModel;

    private Button mAddGrupoButton;
    private RecyclerView mGroupsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private List<Grupo> mGroupList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grupos, container, false);
        viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        mTimeLogHandler = returnTimeLogHandler(getAttachContext(), getActivity().getApplication(), getViewLifecycleOwner());

        mAddGrupoButton = (Button) v.findViewById(R.id.button_add_group);
        mAddGrupoButton.setOnClickListener((view) -> {
            Intent intent = new Intent(getAttachContext(), AddGroupActivity.class);
            giveFeedback(LAUNCH_INTENT, intent);
        });

        mAdapter = returnGruposAdapter(mGroupList, getAttachContext(), mTimeLogHandler);
        mGroupsRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_groups);
        layoutManager = new LinearLayoutManager(getAttachContext());
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mGroupsRecyclerView.setAdapter(mAdapter);

        mAdapter.addFeedbackListener((tipo, feedback, args) -> {
            Intent intent = new Intent(getAttachContext(), ReviewGroupActivity.class);
            intent.putExtra(ReviewGroupActivity.EXTRA_GROUP_ID, feedback.getId());
            intent.putExtra(ReviewGroupActivity.EXTRA_GROUP_NAME, feedback.getNombre());
            giveFeedback(LAUNCH_INTENT, intent);
        });
        mTimeLogHandler.addFeedbackListener((tipo, feedback, args) -> {
            if (tipo == TimeLogHandler.FEEDBACK_LOGGERS_CHANGED) {
                mAdapter.notifyDataSetChanged();
            }
        });

        mGrupoViewModel = new ViewModelProvider(this, viewModelFactory).get(GrupoViewModel.class);
        mGrupoViewModel.getAllGrupos().observe(getViewLifecycleOwner(),(grupos) -> {
            mGroupList = grupos;
            mAdapter.updateDataset(mGroupList);
        });

        return v;
    }

    protected GrupoViewModel getViewModel() {
        return mGrupoViewModel;
    }

    protected abstract TimeLogHandler returnTimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner);

    protected abstract GruposAdapter returnGruposAdapter(List<Grupo> groupList, Context context, TimeLogHandler timeLogHandler);

    public abstract void addGrupoToDb(T grupo);

    public abstract void removeGrupoFromDb(Integer id);

}
