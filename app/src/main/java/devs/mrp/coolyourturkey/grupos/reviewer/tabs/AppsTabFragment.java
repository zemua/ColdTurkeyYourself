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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupViewModel;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaViewModel;
import devs.mrp.coolyourturkey.grupos.GroupType;
import devs.mrp.coolyourturkey.listados.AppLister;
import devs.mrp.coolyourturkey.listados.callables.ListerConstructor;

@AndroidEntryPoint
public class AppsTabFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";
    private static final String KEY_BUNDLE_TYPE = "key.bundle.type";

    private static final String CALLBACK_REMOVE_APP = "callback.remove.app";

    private RecyclerView mRecyclerView;
    private Handler mainHandler;
    private Context mContext;
    private ViewModelProvider.Factory viewModelFactory;
    private Integer mGroupId;
    private AppLister mAppLister;
    private AppsAdapter mAppsAdapter;
    private AplicacionListadaViewModel mAplicacionListadaViewModel;
    private ElementToGroupViewModel elementToGroupViewModel;
    private GroupType type;

    private FutureTask<AppLister> fillAdapterTask;
    private ExecutorService executor = Executors.newFixedThreadPool(1);

    @Inject
    protected DialogWithDelayPresenter dialogWithDelayPresenter;

    public AppsTabFragment(){
        super();
    }

    public AppsTabFragment(GroupType type, Integer groupId) {
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
            setGroupId(savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL));
            this.type = GroupType.valueOf(savedInstanceState.getString(KEY_BUNDLE_TYPE));
        }

        View v = inflater.inflate(R.layout.fragment_single_recycler, container, false);
        mRecyclerView = v.findViewById(R.id.recyclerView);

        mAppsAdapter = new AppsAdapter(mContext, getGroupId());
        mAppsAdapter.resetLoaded();
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.groupAppSpinner);

        fillAdapterTask = new FutureTask<AppLister>(new ListerConstructor(mContext)) {
            @Override
            protected void done() {
                try {
                    mAppLister = get();
                    mainHandler.post(() -> {
                        try {
                            mAplicacionListadaViewModel = new ViewModelProvider(AppsTabFragment.this, viewModelFactory).get(AplicacionListadaViewModel.class);
                            getApps(mAplicacionListadaViewModel).observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
                                @Override
                                public void onChanged(List<AplicacionListada> aplicacionListadas) {
                                    spinner.setVisibility(View.GONE);
                                    mAppsAdapter.updateDataSet(mAppLister.setListedApps(aplicacionListadas));

                                }
                            });
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        mRecyclerView.setAdapter(mAppsAdapter);
        LinearLayoutManager layoutApps = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutApps);

        elementToGroupViewModel = new ViewModelProvider(this, viewModelFactory).get(ElementToGroupViewModel.class);
        elementToGroupViewModel.findElementsOfType(ElementType.APP).observe(getViewLifecycleOwner(), new Observer<List<ElementToGroup>>() {
            @Override
            public void onChanged(List<ElementToGroup> elementToGroups) {
                mAppsAdapter.firstGroupDbLoad(elementToGroups);
            }
        });

        dialogWithDelayPresenter.setListener(CALLBACK_REMOVE_APP, (result, data) -> {
            if (result && data instanceof Integer) {
                elementToGroupViewModel.deleteById((Integer) data);
            } else {
                // if cancelled refresh switches position
                mAppsAdapter.notifyDataSetChanged();
            }
        });
        mAppsAdapter.addFeedbackListener((tipo, feedback, parameters) -> {
            switch (tipo) {
                case AppsAdapter.FEEDBACK_SET_APPTOGROUP:
                    elementToGroupViewModel.insert(feedback);
                    break;
                case AppsAdapter.FEEDBACK_DEL_APPTOGROUP:
                    dialogWithDelayPresenter.showDialog(CALLBACK_REMOVE_APP, feedback.getId());
                    break;
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, getGroupId());
        outState.putString(KEY_BUNDLE_TYPE, this.type.toString());
        super.onSaveInstanceState(outState);
    }

    private LiveData<List<AplicacionListada>> getApps(AplicacionListadaViewModel model) {
        if (GroupType.NEGATIVE.equals(this.type)) {
            return model.getNegativeApps();
        }
        return model.getPositiveApps();
    }

    public void setGroupId(Integer groupId) {
        this.mGroupId = groupId;
    }

    public Integer getGroupId() {
        if (mGroupId == null) {
            return -1;
        }
        return mGroupId;
    }

    @Override
    public void onResume() {
        super.onResume();
        executor.execute(fillAdapterTask);
    }
}
