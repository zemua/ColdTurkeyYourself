package devs.mrp.coolyourturkey.grupospositivos;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupViewModel;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaViewModel;
import devs.mrp.coolyourturkey.listados.AppLister;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class ReviewGroupFragment extends Fragment {

    private static final String TAG = "FRAGMENT_REVIEW_GROUP";

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;

    private TextView textApps;
    private RecyclerView recyclerApps;
    private TextView textConditions;
    private RecyclerView recyclerConditions;
    private Button deleteButton;
    private Button exportButton;

    private ReviewGroupAppsAdapter mAppsAdapter;
    private ReviewGroupsConditionsAdapter mConditionsAdapter;
    private Integer mGroupId;
    private AppLister mAppLister;

    ViewModelProvider.Factory factory;
    private AppToGroupViewModel mAppToGroupViewModel;
    private AplicacionListadaViewModel mAplicacionListadaViewModel;
    private List<AplicacionListada> appsPositivas;
    private List<AppToGroup> groupedApps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //mContext = context;
        //mFeedbackReceiver = (FeedbackReceiver) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());

        mFeedbackReceiver = (FeedbackReceiver) getActivity();
        mContext = getActivity();

        if (savedInstanceState != null && !savedInstanceState.isEmpty()){
            setGroupId(savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL));
        }

        View v = inflater.inflate(R.layout.fragment_reviewgroup, container, false);

        textApps = v.findViewById(R.id.textapps);
        recyclerApps = v.findViewById(R.id.recyclerAppsGrupo);
        textConditions = v.findViewById(R.id.textCondiciones);
        recyclerConditions = v.findViewById(R.id.recyclerCondiciones);
        deleteButton = v.findViewById(R.id.buttonDelete);
        exportButton = v.findViewById(R.id.buttonExp);

        mAppLister = new AppLister(mContext);
        mAppsAdapter = new ReviewGroupAppsAdapter(mAppLister, mContext, getGroupId());
        mAppsAdapter.resetLoaded();
        recyclerApps.setAdapter(mAppsAdapter);
        LinearLayoutManager layoutApps = new LinearLayoutManager(mContext);
        recyclerApps.setLayoutManager(layoutApps);

        mAplicacionListadaViewModel = new ViewModelProvider(this, factory).get(AplicacionListadaViewModel.class);
        mAplicacionListadaViewModel.getPositiveApps().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
            @Override
            public void onChanged(List<AplicacionListada> aplicacionListadas) {
                mAppsAdapter.updateDataSet(aplicacionListadas);
                appsPositivas = aplicacionListadas;
                Log.d(TAG, "updateDataSet with register qty: " + aplicacionListadas.size());
            }
        });

        mAppToGroupViewModel = new ViewModelProvider(this, factory).get(AppToGroupViewModel.class);
        mAppToGroupViewModel.getAllAppToGroup().observe(getViewLifecycleOwner(), new Observer<List<AppToGroup>>() {
            @Override
            public void onChanged(List<AppToGroup> appToGroups) {
                groupedApps = appToGroups;
                mAppsAdapter.firstGroupDbLoad(appToGroups);
                Log.d(TAG, "firstGroupDbLoad with register qty: " + appToGroups.size());
            }
        });

        LinearLayoutManager layoutConditions = new LinearLayoutManager(mContext);
        recyclerConditions.setLayoutManager(layoutConditions);

        mAppsAdapter.addFeedbackListener(new FeedbackListener<AppToGroup>() {
            @Override
            public void giveFeedback(int tipo, AppToGroup feedback, Object... args) {
                switch (tipo) {
                    case ReviewGroupAppsAdapter.FEEDBACK_SET_APPTOGROUP:
                        mAppToGroupViewModel.insert((AppToGroup)feedback);
                        break;
                    case ReviewGroupAppsAdapter.FEEDBACK_DEL_APPTOGROUP:
                        mAppToGroupViewModel.deleteById(((AppToGroup)feedback).getId());
                        break;
                    default:
                        break;
                }
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, getGroupId());
        super.onSaveInstanceState(outState);
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
}
