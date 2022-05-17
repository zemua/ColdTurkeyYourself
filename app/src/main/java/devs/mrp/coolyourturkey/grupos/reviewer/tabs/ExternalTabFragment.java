package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroupViewModel;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;

public class ExternalTabFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";

    private RecyclerView mRecyclerView;
    private Button mButton;
    private Handler mainHandler;
    private Context mContext;
    private ViewModelProvider.Factory viewModelFactory;
    private Integer mGroupId;
    private ExternalAdapter mExternalAdapter;
    private ElementToGroupViewModel elementToGroupViewModel;
    private Type type;

    public ExternalTabFragment(Type type, Integer groupId) {
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

        View v = inflater.inflate(R.layout.fragment_button_and_recycler, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);
        mButton = v.findViewById(R.id.button);

        mExternalAdapter = new ExternalAdapter(mContext, mGroupId);
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.groupAppSpinner);

        elementToGroupViewModel = new ViewModelProvider(this, viewModelFactory).get(ElementToGroupViewModel.class);
        elementToGroupViewModel.findElementsOfGroupAndType(mGroupId, ElementType.FILE).observe(getViewLifecycleOwner(), (elements) -> {
            spinner.setVisibility(View.GONE);
            mExternalAdapter.updateDataSet(elements.stream().map(ElementToGroup::getName).collect(Collectors.toList()));
        });

        mRecyclerView.setAdapter(mExternalAdapter);
        LinearLayoutManager layoutExternal = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutExternal);

        mExternalAdapter.addFeedbackListener((tipo, feedback, parameters) -> {
            switch (tipo) {
                case ExternalAdapter.FEEDBACK_DEL_FILETOGROUP:
                    elementToGroupViewModel.deleteById(feedback.getId());
                    break;
            }
        });

        return v;
    }

    public enum Type {
        POSITIVE, NEGATIVE;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outstate) {
        outstate.putInt(KEY_BUNDLE_ID_ACTUAL, mGroupId);
        super.onSaveInstanceState(outstate);
    }

}
