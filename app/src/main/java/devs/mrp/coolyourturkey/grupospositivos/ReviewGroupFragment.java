package devs.mrp.coolyourturkey.grupospositivos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroup;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupViewModel;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroupViewModel;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoViewModel;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaViewModel;
import devs.mrp.coolyourturkey.listados.AppLister;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class ReviewGroupFragment extends Fragment {

    // TODO implement export time to file

    private static final String TAG = "FRAGMENT_REVIEW_GROUP";

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";

    public static final int FEEDBACK_DELETE_GROUP = 0;
    public static final int FEEDBACK_ADD_CONDITION = 1;
    public static final int FEEDBACK_CLICK_CONDITION = 2;
    public static final int FEEDBACK_EXPORT_TXT = 3;

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;

    private TextView textApps;
    private RecyclerView recyclerApps;
    private TextView textConditions;
    private RecyclerView recyclerConditions;
    private Button deleteButton;
    private Button exportButton;
    private Button mAddConditionButton;

    private ReviewGroupAppsAdapter mAppsAdapter;
    private ReviewGroupsConditionsAdapter mConditionsAdapter;
    private Integer mGroupId;
    private AppLister mAppLister;

    ViewModelProvider.Factory factory;
    private AppToGroupViewModel mAppToGroupViewModel;
    private AplicacionListadaViewModel mAplicacionListadaViewModel;
    private List<AplicacionListada> appsPositivas;
    private List<AppToGroup> groupedApps;
    private ConditionToGroupViewModel mConditionToGroupViewModel;
    private GrupoPositivoViewModel mGrupoPositivoViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.confirmacion);
                builder.setMessage(R.string.seguro_que_deseas_borrar_este_grupo);
                builder.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFeedbackReceiver.receiveFeedback(ReviewGroupFragment.this, FEEDBACK_DELETE_GROUP, getGroupId());
                    }
                });
                builder.setNegativeButton(R.string.conservar, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        exportButton = v.findViewById(R.id.buttonExp);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(ReviewGroupFragment.this, FEEDBACK_EXPORT_TXT, null);
            }
        });

        /**
         * Apps adapter
         */

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

        /**
         * Conditions adapter
         */

        TimeLogHandler logger = new TimeLogHandler(mContext, this.getActivity().getApplication(), this);
        mConditionsAdapter = new ReviewGroupsConditionsAdapter(mContext, logger);
        logger.addFeedbackListener(new FeedbackListener<Object>() {
            @Override
            public void giveFeedback(int tipo, Object feedback, Object... args) {
                if (tipo == TimeLogHandler.FEEDBACK_LOGGERS_CHANGED) {
                    mConditionsAdapter.notifyDataSetChanged();
                }
            }
        });
        recyclerConditions.setAdapter(mConditionsAdapter);
        LinearLayoutManager layoutConditions = new LinearLayoutManager(mContext);
        recyclerConditions.setLayoutManager(layoutConditions);

        mConditionToGroupViewModel = new ViewModelProvider(this, factory).get(ConditionToGroupViewModel.class);
        mConditionToGroupViewModel.findConditionToGroupByGroupId(getGroupId()).observe(this, new Observer<List<ConditionToGroup>>() {
            @Override
            public void onChanged(List<ConditionToGroup> conditionToGroups) {
                mConditionsAdapter.setDataset(conditionToGroups);
            }
        });

        mGrupoPositivoViewModel = new ViewModelProvider(this, factory).get(GrupoPositivoViewModel.class);
        mGrupoPositivoViewModel.getAllGrupos().observe(this, new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                Map<Integer, GrupoPositivo> mapaGrupos = grupoPositivos.stream().collect(Collectors.toMap(GrupoPositivo::getId, g -> g));
                mConditionsAdapter.setGrupos(mapaGrupos);
            }
        });

        mConditionsAdapter.addFeedbackListener(new FeedbackListener<ConditionToGroup>() {
            @Override
            public void giveFeedback(int tipo, ConditionToGroup feedback, Object... args) {
                switch (tipo) {
                    case ReviewGroupsConditionsAdapter.FEEDBACK_CONDITION_SELECTED:
                        mFeedbackReceiver.receiveFeedback(ReviewGroupFragment.this, FEEDBACK_CLICK_CONDITION, feedback);
                        break;
                }
            }
        });

        /**
         *
         */

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

        mAddConditionButton = v.findViewById(R.id.buttonAddCondition);
        mAddConditionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(ReviewGroupFragment.this, FEEDBACK_ADD_CONDITION, null);
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
