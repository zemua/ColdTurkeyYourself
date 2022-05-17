package devs.mrp.coolyourturkey.grupos.grupospositivos_old_deprecated;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
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
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.FTimeBlockFacade;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.ITimeBlockFacade;
import devs.mrp.coolyourturkey.listados.AppLister;
import devs.mrp.coolyourturkey.listados.callables.ListerConstructor;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class ReviewGroupFragment extends Fragment {

    private static final String TAG = "FRAGMENT_REVIEW_GROUP";

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";

    public static final int FEEDBACK_DELETE_GROUP = 0;
    public static final int FEEDBACK_ADD_CONDITION = 1;
    public static final int FEEDBACK_CLICK_CONDITION = 2;
    public static final int FEEDBACK_EXPORT_TXT = 3;
    public static final int FEEDBACK_LIMITS = 4;

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;

    private TextView textApps;
    private TextView textGroupName;
    private RecyclerView recyclerApps;
    private TextView textConditions;
    private RecyclerView recyclerConditions;
    private Button deleteButton;
    private Button exportButton;
    private Button mAddConditionButton;
    private Button mLimitsButton;

    private ReviewGroupAppsAdapter mAppsAdapter;
    private ReviewGroupsConditionsAdapter mConditionsAdapter;
    private Integer mGroupId;
    private String mGroupName;
    private AppLister mAppLister;

    private Handler mainHandler;

    private ViewModelProvider.Factory factory;
    private AppToGroupViewModel mAppToGroupViewModel;
    private AplicacionListadaViewModel mAplicacionListadaViewModel;
    private List<AplicacionListada> appsPositivas;
    private List<AppToGroup> groupedApps;
    private ConditionToGroupViewModel mConditionToGroupViewModel;
    private ITimeBlockFacade mTimeBlockFacade;
    private GrupoPositivoViewModel mGrupoPositivoViewModel;

    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private FutureTask<AppLister> fillAdapterTask;


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
        mainHandler = new Handler(mContext.getMainLooper());

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            setGroupId(savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL));
        }

        View v = inflater.inflate(R.layout.fragment_reviewgroup, container, false);

        textApps = v.findViewById(R.id.textapps);
        textGroupName = v.findViewById(R.id.textGroupNameb);
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

        mLimitsButton = v.findViewById(R.id.buttonLimits);
        mLimitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(ReviewGroupFragment.this, FEEDBACK_LIMITS, null);
            }
        });

        /**
         * Apps adapter
         */

        mAppsAdapter = new ReviewGroupAppsAdapter(mContext, getGroupId());
        mAppsAdapter.resetLoaded();
        ProgressBar spinner = (ProgressBar) v.findViewById(R.id.groupAppSpinner);

        fillAdapterTask = new FutureTask<AppLister>(new ListerConstructor(mContext)) {
            @Override
            protected void done() {
                try {
                    mAppLister = get();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mAppsAdapter.setAppLister(mAppLister);
                                mAplicacionListadaViewModel = new ViewModelProvider(ReviewGroupFragment.this, factory).get(AplicacionListadaViewModel.class);
                                mAplicacionListadaViewModel.getPositiveApps().observe(getViewLifecycleOwner(), new Observer<List<AplicacionListada>>() {
                                    @Override
                                    public void onChanged(List<AplicacionListada> aplicacionListadas) {
                                        spinner.setVisibility(View.GONE);
                                        mAppsAdapter.updateDataSet(aplicacionListadas);
                                        appsPositivas = aplicacionListadas;
                                        Log.d(TAG, "updateDataSet with register qty: " + aplicacionListadas.size());
                                    }
                                });
                            } catch (IllegalStateException e) {
                                Log.d(TAG, "error getting lifecycle owner, you probably skipped this screen before the load ended", e);
                            }
                        }
                    });
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        recyclerApps.setAdapter(mAppsAdapter);
        LinearLayoutManager layoutApps = new LinearLayoutManager(mContext);
        recyclerApps.setLayoutManager(layoutApps);

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
        mConditionToGroupViewModel.findConditionToGroupByGroupId(getGroupId()).observe(this.getViewLifecycleOwner(), new Observer<List<ConditionToGroup>>() {
            @Override
            public void onChanged(List<ConditionToGroup> conditionToGroups) {
                mConditionsAdapter.setDataset(conditionToGroups);
            }
        });

        mGrupoPositivoViewModel = new ViewModelProvider(this, factory).get(GrupoPositivoViewModel.class);
        mGrupoPositivoViewModel.getAllGrupos().observe(this.getViewLifecycleOwner(), new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                Map<Integer, GrupoPositivo> mapaGrupos = grupoPositivos.stream().collect(Collectors.toMap(GrupoPositivo::getId, g -> g));
                mConditionsAdapter.setGrupos(mapaGrupos);
            }
        });

        mTimeBlockFacade = FTimeBlockFacade.getNew(getActivity().getApplication(), getActivity());
        mTimeBlockFacade.getAll((tipo, blocks) -> {
            Map<Integer, AbstractTimeBlock> mapaBlocks = blocks.stream().collect(Collectors.toMap(AbstractTimeBlock::getId, b -> b));
            mConditionsAdapter.setChecks(mapaBlocks);
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
                        mAppToGroupViewModel.insert((AppToGroup) feedback);
                        break;
                    case ReviewGroupAppsAdapter.FEEDBACK_DEL_APPTOGROUP:
                        mAppToGroupViewModel.deleteById(((AppToGroup) feedback).getId());
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

        if (!textGroupName.getText().equals(mGroupName)) {
            textGroupName.setText(mGroupName);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, getGroupId());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        executor.execute(fillAdapterTask);
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

    public void setGroupName(String name) {
        if (textGroupName != null) {
            textGroupName.setText(name);
        }
        this.mGroupName = name;
    }

    public String getGroupName() {
        return this.mGroupName;
    }
}
