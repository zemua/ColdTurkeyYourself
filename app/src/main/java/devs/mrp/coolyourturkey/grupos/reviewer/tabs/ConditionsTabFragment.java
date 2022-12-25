package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.IntentAttacher;
import devs.mrp.coolyourturkey.comun.impl.IntentAttacherImpl;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoViewModel;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoConditionViewModel;
import devs.mrp.coolyourturkey.grupos.GroupType;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.addcondition.AddConditionActivity;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.addcondition.ConditionActionConstants;

@AndroidEntryPoint
public class ConditionsTabFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";
    private static final String KEY_BUNDLE_NAME = "key.bundle.name";
    private static final String KEY_BUNDLE_TYPE = "key.bundle.type";
    private static final String KEY_BUNDLE_PREVENT_CLOSE = "key.bundle.prevent.close";

    private static final String REQUEST_KEY_PREVENT_CLOSE = "request.key.prevent.close";

    private Context mContext;
    private Integer mGroupId;
    private String mGroupName;
    private boolean mGroupPreventClose;
    private ViewModelProvider.Factory viewModelFactory;
    private GrupoViewModel grupoViewModel;
    private GrupoConditionViewModel grupoConditionViewModel;
    private RecyclerView mRecyclerView;
    private Button mButton;
    private Switch mSecondButton;
    private AdapterHandler<GrupoCondition> mConditionsAdapterHandler;
    private GroupType type;
    private List<GrupoCondition> handlerDataset;

    @Inject
    protected AdapterHandlerFactory<GrupoCondition> adapterHandlerFactory;
    @Inject
    protected ViewModelProvider mViewModelProvider;
    @Inject
    protected DialogWithDelayPresenter dialogWithDelayPresenter;

    public ConditionsTabFragment() {
        super();
    }

    public ConditionsTabFragment(Integer groupId, String groupName, GroupType type, boolean preventClose) {
        super();
        this.mGroupId = groupId;
        this.mGroupName = groupName;
        this.type = type;
        this.mGroupPreventClose = preventClose;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        mContext = getActivity();

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL);
            mGroupName = savedInstanceState.getString(KEY_BUNDLE_NAME);
            type = GroupType.valueOf(savedInstanceState.getString(KEY_BUNDLE_TYPE));
            mGroupPreventClose = savedInstanceState.getBoolean(KEY_BUNDLE_PREVENT_CLOSE);
        }

        View v = inflater.inflate(R.layout.fragment_button_second_button_and_recycler, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mButton = v.findViewById(R.id.button);

        mConditionsAdapterHandler = adapterHandlerFactory.getHandler(type, getViewLifecycleOwner());
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.groupAppSpinner);

        grupoConditionViewModel = mViewModelProvider.get(GrupoConditionViewModel.class);
        grupoConditionViewModel.findConditionsByGroupId(mGroupId).observe(getViewLifecycleOwner(), (conditions) -> {
            spinner.setVisibility(View.GONE);
            handlerDataset = conditions;
            mConditionsAdapterHandler.setDataset(type.equals(GroupType.NEGATIVE) && mGroupPreventClose ? Collections.EMPTY_LIST : handlerDataset);
        });

        mRecyclerView.setAdapter(mConditionsAdapterHandler.getAdapter());
        LinearLayoutManager layoutConditions = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutConditions);

        grupoViewModel = mViewModelProvider.get(GrupoViewModel.class);
        grupoViewModel.findAllGruposPositivos().observe(getViewLifecycleOwner(), (grupos) -> {
            mConditionsAdapterHandler.setGrupos(grupos.stream().collect(Collectors.toMap(Grupo::getId, Function.identity())));
        });

        mConditionsAdapterHandler.addFeedbackListener((tipo, feedback, parameters) -> {
            switch (tipo) {
                case AdapterHandler.FEEDBACK_ITEM_SELECTED:
                    launchEditCondition(feedback);
                    break;
            }
        });

        mButton.setText(R.string.anhadir);
        mButton.setOnClickListener((view) -> launchAddCondition());

        mSecondButton = v.findViewById(R.id.second_button);
        setupPreventCloseButton(mSecondButton);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mConditionsAdapterHandler.refresh();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, mGroupId);
        outState.putString(KEY_BUNDLE_NAME, mGroupName);
        outState.putString(KEY_BUNDLE_TYPE, type.toString());
        outState.putBoolean(KEY_BUNDLE_PREVENT_CLOSE, mGroupPreventClose);
        super.onSaveInstanceState(outState);
    }

    private void launchEditCondition(GrupoCondition condition) {
        Intent intent = getIntentForAddConditionActivity();
        IntentAttacher attacher = new IntentAttacherImpl(intent);
        attacher.attach(ConditionActionConstants.EXTRA_GROUP_CONDITION, condition);
        startActivity(intent);
    }

    private void launchAddCondition() {
        Intent intent = getIntentForAddConditionActivity();
        startActivity(intent);
    }

    private Intent getIntentForAddConditionActivity() {
        Intent intent = new Intent(getActivity(), AddConditionActivity.class);
        intent.putExtra(ConditionActionConstants.EXTRA_GROUP_ID, mGroupId);
        intent.putExtra(ConditionActionConstants.EXTRA_GROUP_NAME, mGroupName);
        return intent;
    }

    private void setupPreventCloseButton(Switch button) {
        if (!type.equals(GroupType.NEGATIVE)) {
            mSecondButton.setVisibility(View.GONE);
            return;
        }
        button.setText(R.string.evitar_cierre);
        mSecondButton.setChecked(mGroupPreventClose);
        setConditionsEnabled(!mGroupPreventClose);

        mSecondButton.setOnClickListener(v -> {
            if (mSecondButton.isChecked()) {
                mSecondButton.setChecked(false);
                dialogWithDelayPresenter.showDialog(REQUEST_KEY_PREVENT_CLOSE);
            } else {
                grupoViewModel.setPreventCloseForGroupId(false, mGroupId);
                mGroupPreventClose = false;
                setConditionsEnabled(true);
            }
        });

        dialogWithDelayPresenter.setListener(REQUEST_KEY_PREVENT_CLOSE,result ->{
            if (result) {
                mSecondButton.setChecked(true);
                grupoViewModel.setPreventCloseForGroupId(true, mGroupId);
                mGroupPreventClose = true;
                setConditionsEnabled(false);
            }
        });
    }

    private void setConditionsEnabled(boolean yesNo) {
        mButton.setEnabled(yesNo);
        if (yesNo) {
            mConditionsAdapterHandler.setDataset(handlerDataset != null ? handlerDataset : Collections.EMPTY_LIST);
        } else {
            mConditionsAdapterHandler.setDataset(Collections.EMPTY_LIST);
        }
    }

}
