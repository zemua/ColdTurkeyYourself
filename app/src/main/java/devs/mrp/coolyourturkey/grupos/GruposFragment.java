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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoViewModel;
import devs.mrp.coolyourturkey.grupos.grupospositivos_old_deprecated.AddGroupActivity;
import devs.mrp.coolyourturkey.grupos.reviewer.ReviewerActivity;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public abstract class GruposFragment<T extends Grupo> extends FeedbackerFragment<Intent> {

    public static final int ADD_INTENT = 0;
    public static final int REVIEW_INTENT = 1;

    private ViewModelProvider.Factory viewModelFactory;
    private TimeLogHandler mTimeLogHandler;
    protected GruposAdapter mAdapter;
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
            giveFeedback(ADD_INTENT, intent);
        });

        mAdapter = returnGruposAdapter(mGroupList, getAttachContext(), mTimeLogHandler);
        mGroupsRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_groups);
        layoutManager = new LinearLayoutManager(getAttachContext());
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mGroupsRecyclerView.setAdapter(mAdapter);

        mAdapter.addFeedbackListener((tipo, feedback, args) -> {
            Intent intent = new Intent(getAttachContext(), ReviewerActivity.class);
            intent.putExtra(ReviewerActivity.EXTRA_GROUP_ID, feedback.getId());
            intent.putExtra(ReviewerActivity.EXTRA_GROUP_NAME, feedback.getNombre());
            intent.putExtra(ReviewerActivity.EXTRA_GROUP_TYPE, feedback.getType().toString());
            giveFeedback(REVIEW_INTENT, intent);
        });
        mTimeLogHandler.addFeedbackListener((tipo, feedback, args) -> {
            if (tipo == TimeLogHandler.FEEDBACK_LOGGERS_CHANGED) {
                mAdapter.notifyDataSetChanged();
            }
        });

        mGrupoViewModel = new ViewModelProvider(this, viewModelFactory).get(GrupoViewModel.class);
        findGrupos().observe(getViewLifecycleOwner(),(grupos) -> {
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

    protected abstract LiveData<List<Grupo>> findGrupos();

}
