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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;

public class ConditionsTabFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";
    private static final String KEY_BUNDLE_NAME_ACTUAL = "key.bundle.name.actual";
    private static final String KEY_BUNDLE_GRUPOS_LIST = "key.bundle.grupos.list";
    private static final String KEY_BUNDLE_CONDITION_FOR_EDIT = "key.bundle.condition.for.edit";
    private static final String KEY_BUNDLE_IF_IS_EDIT_ACTION = "key.bundle.if.is.edit.action";

    private Context mContext;
    private Integer mGroupId;
    private String mGroupName;
    private List<Grupo> mGrupos;
    private boolean mIsEditAction = false;
    private GrupoCondition mGrupoCondition;
    private ConditionHelper mConditionHelper = new ConditionHelper();
    private ViewModelProvider.Factory viewModelFactory;
    private Handler mainHandler;
    private RecyclerView mRecyclerView;
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
        mainHandler = new Handler(mContext.getMainLooper());

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL);
        }

        View v = inflater.inflate(R.layout.fragment_button_and_recycler, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);

        mConditionsAdapterHandler = new ConditionsAdapterHandlerImpl(mContext);
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.groupAppSpinner);



        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, mGroupId);
        super.onSaveInstanceState(outState);
    }

}
