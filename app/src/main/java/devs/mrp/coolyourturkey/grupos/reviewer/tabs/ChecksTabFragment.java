package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlock;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlockViewModel;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupViewModel;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.grupos.GroupType;

public class ChecksTabFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";

    private RecyclerView mRecyclerView;
    private Handler mainHandler;
    private Context mContext;
    private ViewModelProvider.Factory viewModelFactory;
    private Integer mGroupId;
    private ElementToGroupViewModel elementToGroupViewModel;
    private GroupType type;

    private CheckTimeBlockViewModel checkTimeBlockViewModel;
    private ChecksAdapter mChecksAdapter;

    public ChecksTabFragment(GroupType type, Integer groupId) {
        super();
        this.type = type;
        this.mGroupId = groupId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());

        mContext = getActivity();
        mainHandler = new Handler(mContext.getMainLooper());

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL);
        }

        View v = inflater.inflate(R.layout.fragment_single_recycler, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);

        mChecksAdapter = new ChecksAdapter(mContext, mGroupId);
        mChecksAdapter.resetLoaded();
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.groupAppSpinner);

        checkTimeBlockViewModel = new ViewModelProvider(ChecksTabFragment.this, viewModelFactory).get(CheckTimeBlockViewModel.class);
        checkTimeBlockViewModel.findAllTimeBlocks().observe(getViewLifecycleOwner(), (checkTimeBlocks) -> {
            spinner.setVisibility(View.GONE);
            mChecksAdapter.updateDataSet(checkTimeBlocks);
        });

        mRecyclerView.setAdapter(mChecksAdapter);
        LinearLayoutManager layoutChecks = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutChecks);

        elementToGroupViewModel = new ViewModelProvider(this, viewModelFactory).get(ElementToGroupViewModel.class);
        elementToGroupViewModel.findElementsOfType(ElementType.CHECK).observe(getViewLifecycleOwner(), (elementToGroups) -> {
            mChecksAdapter.firstGroupDbLoad(elementToGroups);
        });

        mChecksAdapter.addFeedbackListener((tipo, feedback, parameters) -> {
            switch (tipo) {
                case ChecksAdapter.FEEDBACK_SET_CHECKTOGROUP:
                    elementToGroupViewModel.insert(feedback);
                    break;
                case ChecksAdapter.FEEDBACK_DEL_CHECKTOGROUP:
                    elementToGroupViewModel.deleteById(feedback.getId());
                    break;
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mChecksAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, mGroupId);
        super.onSaveInstanceState(outState);
    }

    private LiveData<List<CheckTimeBlock>> getChecks(CheckTimeBlockViewModel model) {
        if (GroupType.POSITIVE.equals(this.type)) {
            return model.findAllTimeBlocks();
        }
        return null;
    }

    public Integer getGroupId() {
        if (mGroupId == null) {
            return -1;
        }
        return mGroupId;
    }

}
