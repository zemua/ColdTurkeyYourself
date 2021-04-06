package devs.mrp.coolyourturkey;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.R;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.comun.PermisosChecker;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMapViewModel;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;
import devs.mrp.coolyourturkey.watchdog.TimeAssembler;
import devs.mrp.coolyourturkey.watchdog.WatchdogService;
import devs.mrp.coolyourturkey.watchdog.WatchdogStarter;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    private static final String DIALOG_PERMISOS = "DialogPermisos";
    public static final int FEEDBACK_POSITIVAS = 0;
    public static final int FEEDBACK_NEGATIVAS = 1;
    public static final int FEEDBACK_TIEMPO_POSITIVO = 4;
    public static final int FEEDBACK_TIEMPO_NEGATIVO = 5;
    public static final int REQUEST_PERMISO_USO = 2;
    public static final int REQUEST_PERMISO_ALERTA = 7;
    public static final int FEEDBACK_REQ_PERMISO_USAGE = 3;
    public static final int FEEDBACK_TO_CONFIG = 6;
    public static final int FEEDBACK_REQ_PERMISO_ALERTA = 8;
    public static final int FEEDBACK_TIEMPO_DOBLE = 9;
    public static final int FEEDBACK_GRUPOS_POSITIVOS = 10;

    private static final String EXTRA_SWITCH_POSITION = "extra switch position";

    FeedbackReceiver<Fragment, Object> mFeedbackReceiver;
    Context mContext;

    private Button mPositiveButton;
    private Button mPositiveGroupsButton;
    private Button mNegativeButton;
    private Button mTiempoButton;
    private Button mPositiveTime;
    private Button mNegativeTime;
    private Button mConfigButton;
    private Switch mServiceSwitch;
    private TextView mTiempoActual;
    private ValueMapViewModel mValueMapViewModel;
    private WatchdogStarter mWatchDogStarter;
    private WatchdogService mService;
    private boolean mBound = false;
    private TimeAssembler mTimeAssembler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        if (savedInstanceState != null) {
            boolean lswitchpos = savedInstanceState.getBoolean(EXTRA_SWITCH_POSITION);
            if (lswitchpos == false && mServiceSwitch != null && mServiceSwitch.isChecked()) {
                mServiceSwitch.setChecked(false);
            } else if (lswitchpos == true && mServiceSwitch != null && !mServiceSwitch.isChecked()) {
                mServiceSwitch.setChecked(true);
            }
        }

         */
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTimeAssembler != null && mTiempoActual != null) {
            mTiempoActual.setText(getString(R.string.tiempo_restante).concat(MilisToTime.getFormated(mTimeAssembler.getLast())));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        //outstate.putBoolean(EXTRA_SWITCH_POSITION, mServiceSwitch.isChecked());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFeedbackReceiver = (FeedbackReceiver) context;
        mWatchDogStarter = new WatchdogStarter(this.getActivity().getApplication(), connection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mPositiveButton = (Button) v.findViewById(R.id.positivas);
        mPositiveGroupsButton = (Button) v.findViewById(R.id.grupos_positivos);
        mNegativeButton = (Button) v.findViewById(R.id.negativas);
        //mPositiveTime = (Button) v.findViewById(R.id.ver_tiempo_positivo);
        //mNegativeTime = (Button) v.findViewById(R.id.ver_tiempo_negativo);
        mTiempoButton = (Button) v.findViewById(R.id.ver_tiempo_doble);
        mTiempoActual = (TextView) v.findViewById(R.id.text_tiempo_actual);

        mConfigButton = (Button) v.findViewById(R.id.to_config_button);
        mWatchDogStarter.startService(); // Se quitó el switch y se pone esto para empezarlo si o si
        //mServiceSwitch = (Switch) v.findViewById(R.id.switch_servicio);

        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(MainFragment.this, FEEDBACK_POSITIVAS, mPositiveButton);
            }
        });

        mPositiveGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(MainFragment.this, FEEDBACK_GRUPOS_POSITIVOS, mPositiveGroupsButton);
            }
        });

        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(MainFragment.this, FEEDBACK_NEGATIVAS, mNegativeButton);
            }
        });

        /*
        mPositiveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(MainFragment.this, FEEDBACK_TIEMPO_POSITIVO, mPositiveTime);
            }
        });

        mNegativeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(MainFragment.this, FEEDBACK_TIEMPO_NEGATIVO, mNegativeTime);
            }
        });
        */

        mTiempoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(MainFragment.this, FEEDBACK_TIEMPO_DOBLE, mTiempoButton);
            }
        });

        mConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(MainFragment.this, FEEDBACK_TO_CONFIG, mConfigButton);
            }
        });

        /*
        mValueMapViewModel = new ViewModelProvider(MainFragment.this).get(ValueMapViewModel.class);
        mValueMapViewModel.getValueOf(WatchdogHandler.WATCHDOG_ACTIVO_DB_ID).observe(getViewLifecycleOwner(), new Observer<List<ValueMap>>() {
            @Override
            public void onChanged(List<ValueMap> valueMaps) {
                if (valueMaps.size() == 0) {
                    // todavía no se ha agregado esta entrada, lo agregamos como true y activamos el switch
                    if (!mServiceSwitch.isChecked()) {
                        mServiceSwitch.setChecked(true);
                        mWatchDogStarter.startService();
                    }
                    mValueMapViewModel.insert(new ValueMap(WatchdogHandler.WATCHDOG_ACTIVO_DB_ID, ValueMap.VALOR_TRUE));
                } else if (valueMaps.get(0).getValor().equals(ValueMap.VALOR_FALSE)) {
                    if (mServiceSwitch.isChecked()) {
                        mServiceSwitch.setChecked(false);
                        if (mBound) {
                            mWatchDogStarter.stopService(mService);
                        }
                    }
                } else {
                    if (!mServiceSwitch.isChecked()) {
                        mServiceSwitch.setChecked(true);
                        mWatchDogStarter.startService();
                    }
                }
            }
        });
         */

        /*
        mServiceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mServiceSwitch.isChecked()) {
                    mValueMapViewModel.insert(new ValueMap(WatchdogHandler.WATCHDOG_ACTIVO_DB_ID, ValueMap.VALOR_FALSE));
                    if (mBound){
                        mWatchDogStarter.stopService(mService);
                    }
                } else {
                    mValueMapViewModel.insert(new ValueMap(WatchdogHandler.WATCHDOG_ACTIVO_DB_ID, ValueMap.VALOR_TRUE));
                    mWatchDogStarter.startService();
                }
            }
        });

         */

        mTimeAssembler = new TimeAssembler(this.getActivity().getApplication(), this);
        mTimeAssembler.addFeedbackListener((tipo, feedback, args) -> {
            if (tipo == TimeAssembler.FEEDBACK_SUMATORIO) {
                mTiempoActual.setText(getString(R.string.tiempo_restante).concat(MilisToTime.getFormated(feedback)));
            }
        });

        return v;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WatchdogService.LocalBinder binder = (WatchdogService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public WatchdogService getService() {
        return mService;
    }

    public void muestraDialogoPermisos(FragmentManager fm, int requestPermisoTipo) {
        String titulo = "";
        String mensaje = "";
        if (requestPermisoTipo == REQUEST_PERMISO_USO) {
            titulo = this.getString(R.string.explicacion_necesita_permiso_uso);
            mensaje = this.getString(R.string.explicacion_mensaje_permiso_uso)
                    .concat("\n\n\n")
                    .concat(this.getString(R.string.consulta_politica_privacidad));
        } else if (requestPermisoTipo == REQUEST_PERMISO_ALERTA) {
            titulo = this.getString(R.string.explicacion_necesita_permiso_alertas);
            mensaje = this.getString(R.string.explicacion_mensaje_permiso_alertas);
        }
        PermisosChecker.muestraDialogoPermisos(fm, MainFragment.this, requestPermisoTipo, titulo, mensaje);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PERMISO_USO) {
            Boolean aceptado = data.getBooleanExtra(PermisosDialogFragment.EXTRA_PERMISO, true);
            mFeedbackReceiver.receiveFeedback(MainFragment.this, FEEDBACK_REQ_PERMISO_USAGE, aceptado);
        } else if (requestCode == REQUEST_PERMISO_ALERTA) {
            Boolean aceptado = data.getBooleanExtra(PermisosDialogFragment.EXTRA_PERMISO, true);
            mFeedbackReceiver.receiveFeedback(MainFragment.this, FEEDBACK_REQ_PERMISO_ALERTA, aceptado);
        }
    }
}
