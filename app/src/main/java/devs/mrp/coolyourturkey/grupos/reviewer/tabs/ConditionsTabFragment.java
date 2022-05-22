package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroupViewModel;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoViewModel;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoConditionViewModel;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.addcondition.AddConditionActivity;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.addcondition.ConditionActionConstants;

public class ConditionsTabFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";

    private Context mContext;
    private Integer mGroupId;
    private String mGroupName;
    private ViewModelProvider.Factory viewModelFactory;
    private GrupoViewModel grupoViewModel;
    private GrupoConditionViewModel grupoConditionViewModel;
    private RecyclerView mRecyclerView;
    private Button mButton;
    private AdapterHandler<GrupoCondition> mConditionsAdapterHandler;

    public ConditionsTabFragment(Integer groupId, String groupName) {
        super();
        this.mGroupId = groupId;
        this.mGroupName = groupName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        mContext = getActivity();

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL);
        }

        View v = inflater.inflate(R.layout.fragment_button_and_recycler, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mButton = v.findViewById(R.id.button);

        mConditionsAdapterHandler = new ConditionsAdapterHandlerImpl(mContext);
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.groupAppSpinner);

        grupoConditionViewModel = new ViewModelProvider(ConditionsTabFragment.this, viewModelFactory).get(GrupoConditionViewModel.class);
        grupoConditionViewModel.findConditionsByGroupId(mGroupId).observe(getViewLifecycleOwner(), (conditions) -> {
            spinner.setVisibility(View.GONE);
            mConditionsAdapterHandler.setDataset(conditions);
        });

        mRecyclerView.setAdapter(mConditionsAdapterHandler.getAdapter());
        LinearLayoutManager layoutConditions = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutConditions);

        grupoViewModel = new ViewModelProvider(this, viewModelFactory).get(GrupoViewModel.class);
        grupoViewModel.findAllGruposPositivos().observe(getViewLifecycleOwner(), (grupos) -> {
            mConditionsAdapterHandler.setGrupos(grupos.stream().collect(Collectors.toMap(Grupo::getId, Function.identity())));
        });

        mConditionsAdapterHandler.addFeedbackListener((tipo, feedback, parameters) -> {
            switch (tipo) {
                case AdapterHandler.FEEDBACK_ITEM_SELECTED:
                    // TODO call to edit condition activity
                    break;
            }
        });

        mButton.setText(R.string.anhadir);
        mButton.setOnClickListener((view) -> launchAddCondition());

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, mGroupId);
        super.onSaveInstanceState(outState);
    }

    private void launchAddCondition() {
        Intent intent = new Intent(getActivity(), AddConditionActivity.class);
        intent.putExtra(ConditionActionConstants.EXTRA_GROUP_ID, mGroupId);
        intent.putExtra(ConditionActionConstants.EXTRA_GROUP_NAME, mGroupName);
        startActivity(intent);
    }

}
