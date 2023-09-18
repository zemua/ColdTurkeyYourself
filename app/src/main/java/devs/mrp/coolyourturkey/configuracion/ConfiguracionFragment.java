package devs.mrp.coolyourturkey.configuracion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.DialogDelayer;
import devs.mrp.coolyourturkey.comun.DialogWithDelay;
import devs.mrp.coolyourturkey.comun.TimePickerFragment;
import devs.mrp.coolyourturkey.comun.UriUtils;
import devs.mrp.coolyourturkey.comun.ViewDisabler;
import devs.mrp.coolyourturkey.comun.ViewDisablerSupplier;
import devs.mrp.coolyourturkey.configuracion.modules.builder.PreferencesSwitchBuilderProvider;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.Importables;
import devs.mrp.coolyourturkey.databaseroom.urisimportar.ImportablesViewModel;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMap;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMapViewModel;
import devs.mrp.coolyourturkey.watchdog.Exporter;
import devs.mrp.coolyourturkey.watchdog.Importer;

@AndroidEntryPoint
public class ConfiguracionFragment extends Fragment {

    private static String TAG = "CONFIGURACION FRAGMENT";

    private static int REQUEST_CODE_WRITE = 20;
    private static int REQUEST_CODE_READ = 21;
    private static int REQUEST_CODE_MENOS_PROPORCION = 22;
    private static int REQUEST_CODE_DESACTIVA_TIEMPO_GRACIA = 23;
    private static int REQUEST_CODE_TIME_PIQUER_INICIO = 24;
    private static int REQUEST_CODE_TIME_PIQUER_FIN = 25;
    private static int REQUEST_CODE_MOLESTO_TIME_INICIO = 26;
    private static int REQUEST_CODE_MOLESTO_TIME_FIN = 27;
    private static int REQUEST_CODE_MOLESTO_ACTIVA_TOQUE = 28;

    private static String TAG_DIALOGO_CON_DELAY = "Dialogo.con.delay.configuracion.fragment.java";
    private static String TAG_DIALOGO_POLITICA_PRIVACIDAD = "dualogo.politica.privacidad.configuracion.fragment.java";

    public static String TEXT_MIME_TYPE = "text/plain";
    private static String IMAGE_MIME_TYPE = "image/png";
    private static String TEXT_FILE_NAME = "_tfn_cyt.txt";
    public static String EXPORT_TXT_VALUE_MAP_KEY = "export txt uri key";
    public static String EXPORT_TXT_YES_NO_KEY = "export txt yes or not";
    public static String IMPORT_TXT_YES_NO_KEY = "import txt currently or not";
    public static String TRUE = "true";
    public static String FALSE = "false";

    @Inject
    PreferencesSwitchBuilderProvider preferencesSwitchBuilderProvider;
    @Inject
    ViewDisablerSupplier viewDisablerSupplier;

    private ViewDisabler viewDisabler;

    private Context mContext;
    ColorStateList oldColors;
    private Switch mShareSwitch;
    private Button mShareButton;
    private TextView mShareText;
    private ValueMapViewModel mValueMapViewModel;
    private boolean mSwitchExportSetted = false;
    private Switch mImportSwitch;
    private Button mImportButton;
    private Button mRestaMinuto;
    private Button mSumaMinuto;
    private TextView mTextoMinutosIntervalo;
    private Button mProporcionMenos;
    private TextView mTextoProporcion;
    private Button mProporcionMas;
    private Button mMenosAvisoRestantes;
    private Button mMasAvisoRestantes;
    private TextView mTextoAvisoRestantes;
    private Switch mSwitchTiempoDeGracia;
    private Switch mSwitchAvisoCambio;
    private Button mToqueQuedaInicio;
    private Button mToqueQuedaFin;
    private TextView mTextToqueDeQuedaMinutosAviso;
    private Button mButtonMenosToqueDeQuedaMinutosAviso;
    private Button mButtonMasToqueDeQuedaMinutosAviso;
    private Switch mSwitchAvisoToqueDeQueda;
    private Switch mSwitchActivaToqueDeQueda;
    private RecyclerView mImportRecyclerView;
    private Button mButtonPoliticaPrivacidad;
    private Switch mSwitchNotifyConditionsNotMet;
    private Switch mSwitchNotifyConditionsRecentlyMet;

    private Button mButtonChangeOfDayMinus;
    private Button mButtonChangeOfDayPlus;
    private TextView mTextChangeOfDay;

    private Switch mSwitchNotifyChangeOfDay;

    private Button mButtonNotifyChangeOfDayMinus;
    private Button mButtonNotifyChangeOfDayPlus;
    private TextView mTextNotifyChangeOfDayMinutes;

    private ImportablesViewModel mImportablesViewModel;
    private DialogWithDelay mDialogo;
    private DialogDelayer mDialogDelayer;
    private boolean mSwitchImportSetted = false;
    private MisPreferencias mMisPreferencias;

    private boolean mLoadedExports = false;
    private boolean mLoadedImports = false;
    private ValueMap mExportValueMap;
    private List<Importables> mImportablesList;

    ViewModelProvider.Factory factory;

    @Override
    public void onCreate(Bundle savedinstanceState) {
        super.onCreate(savedinstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
    }

    @Override
    public void onAttach(Context context) {
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        super.onAttach(context);
        mContext = context;
        mValueMapViewModel = new ViewModelProvider(this, factory).get(ValueMapViewModel.class);
        mImportablesViewModel = new ViewModelProvider(this, factory).get(ImportablesViewModel.class);
        mMisPreferencias = new MisPreferencias(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewDisabler = viewDisablerSupplier.get();

        View v = inflater.inflate(R.layout.fragment_configuracion, container, false);
        mShareSwitch = (Switch) v.findViewById(R.id.config_switch_share_time);
        mShareButton = (Button) v.findViewById(R.id.config_button_share_time);
        mShareText = (TextView) v.findViewById(R.id.config_text_share_item);
        mImportSwitch = (Switch) v.findViewById(R.id.config_switch_import_time);
        mImportButton = (Button) v.findViewById(R.id.config_button_import_time);
        mImportRecyclerView = (RecyclerView) v.findViewById(R.id.config_recycler_import_time);
        mSumaMinuto = (Button) v.findViewById(R.id.buttonMinutoMas);
        mRestaMinuto = (Button) v.findViewById(R.id.buttonMinutoMenos);
        mTextoMinutosIntervalo = (TextView) v.findViewById(R.id.textoMinutosIntervalo);
        mProporcionMas = (Button) v.findViewById(R.id.proporcionMas);
        mProporcionMenos = (Button) v.findViewById(R.id.proporcionMenos);
        mTextoProporcion = (TextView) v.findViewById(R.id.textoProporcion);
        mMenosAvisoRestantes = (Button) v.findViewById(R.id.botonMenosAvisoRestantes);
        mMasAvisoRestantes = (Button) v.findViewById(R.id.botonMasAvisoRestantes);
        mTextoAvisoRestantes = (TextView) v.findViewById(R.id.textoAvisoRestantes);
        mSwitchTiempoDeGracia = (Switch) v.findViewById(R.id.switchTiempoDeGracia);
        mSwitchAvisoCambio = (Switch) v.findViewById(R.id.switchAvisoCambio);
        mToqueQuedaInicio = (Button) v.findViewById(R.id.buttonInicioToqueDeQueda);
        mToqueQuedaFin = (Button) v.findViewById(R.id.buttonFinToqueDeQueda);
        mButtonMenosToqueDeQuedaMinutosAviso = (Button) v.findViewById(R.id.buttonMenosAvisoToqueDeQueda);
        mButtonMasToqueDeQuedaMinutosAviso = (Button) v.findViewById(R.id.buttonMasAvisoToqueDeQueda);
        mTextToqueDeQuedaMinutosAviso = (TextView) v.findViewById(R.id.textMinutoToqueDeQueda);
        mSwitchAvisoToqueDeQueda = (Switch) v.findViewById(R.id.switchAvisoToqueDeQueda);
        mSwitchActivaToqueDeQueda = (Switch) v.findViewById(R.id.switchActivaToque);
        mButtonPoliticaPrivacidad = (Button) v.findViewById(R.id.bPoliticaPrivacidad);
        mSwitchNotifyConditionsNotMet = (Switch) v.findViewById(R.id.switchNotifyConditions);
        mSwitchNotifyConditionsRecentlyMet = (Switch) v.findViewById(R.id.switchNotifyConditionsMet);

        mButtonChangeOfDayMinus = (Button) v.findViewById(R.id.minusChangeOfDay);
        mButtonChangeOfDayPlus = (Button) v.findViewById(R.id.plusChangeOfDay);
        mTextChangeOfDay = (TextView) v.findViewById(R.id.textHourChangeOfDay);

        mSwitchNotifyChangeOfDay = (Switch) v.findViewById(R.id.switchNotifyChangeOfDay);

        mButtonNotifyChangeOfDayMinus = (Button) v.findViewById(R.id.minusWarnChangeOfDay);
        mButtonNotifyChangeOfDayPlus = (Button) v.findViewById(R.id.plusWarnChangeOfDay);
        mTextNotifyChangeOfDayMinutes = (TextView) v.findViewById(R.id.textWarnHourChangeOfDay);

        Optional<Switch> decreasePositiveSwitch = preferencesSwitchBuilderProvider.get()
                .parentElement(v)
                .repositoryIdentifier(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DECREASE)
                .viewResourceId(R.id.decreasePositiveLockdown)
                .actionOnStateChange(() -> viewDisabler.evaluateConditions())
                .viewDisabler(viewDisabler)
                .addRequiredTrueEnablers(() -> mSwitchActivaToqueDeQueda.isChecked())
                .conditionForNegative(s -> !s.isChecked())
                .configure()
                .buildElement();

        preferencesSwitchBuilderProvider.get()
                .parentElement(v)
                .repositoryIdentifier(PreferencesBooleanEnum.LOCKDOWN_NEGATIVE_BLOCK)
                .viewResourceId(R.id.closeNegativeLockdown)
                .viewDisabler(viewDisabler)
                .addRequiredTrueEnablers(() -> mSwitchActivaToqueDeQueda.isChecked())
                .conditionForNegative(s -> !s.isChecked())
                .configure()
                .buildElement();

        preferencesSwitchBuilderProvider.get()
                .parentElement(v)
                .repositoryIdentifier(PreferencesBooleanEnum.LOCKDOWN_NEUTRAL_DECREASE)
                .viewResourceId(R.id.decreaseNeutralLockdown)
                .viewDisabler(viewDisabler)
                .addRequiredTrueEnablers(() -> mSwitchActivaToqueDeQueda.isChecked())
                .conditionForNegative(s -> !s.isChecked())
                .configure()
                .buildElement();

        preferencesSwitchBuilderProvider.get()
                .parentElement(v)
                .repositoryIdentifier(PreferencesBooleanEnum.LOCKDOWN_POSITIVE_DONT_SUM)
                .viewResourceId(R.id.dontSumPositiveLockdown)
                .viewDisabler(viewDisabler)
                .addRequiredTrueEnablers(() -> mSwitchActivaToqueDeQueda.isChecked())
                .addRequiredTrueEnablers(() -> decreasePositiveSwitch.map(s -> !s.isChecked()).orElse(true))
                .conditionForNegative(s -> !s.isChecked())
                .configure()
                .buildElement();

        LiveData<List<ValueMap>> lvalueExport = mValueMapViewModel.getValueOf(EXPORT_TXT_VALUE_MAP_KEY);
        lvalueExport.observe(getViewLifecycleOwner(), new Observer<List<ValueMap>>() {
            @Override
            public void onChanged(List<ValueMap> valueMaps) {
                mLoadedExports = true;
                if (valueMaps.size() > 0 && Exporter.tenemosPermisoEscritura(mContext, Uri.parse(valueMaps.get(0).getValor()))) {
                    mExportValueMap = valueMaps.get(0);
                    //mShareText.setText(valueMaps.get(0).getValor());
                    Uri luri = Uri.parse(valueMaps.get(0).getValor());
                    mShareText.setText(UriUtils.getFileName(luri, mContext));
                    if (oldColors == null) {
                        oldColors = mShareText.getTextColors();
                    }
                    if (!Exporter.tenemosPermisoEscritura(mContext, luri)) {
                        mShareText.setTextColor(Color.RED);
                        mShareText.setPaintFlags(mShareText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        mShareText.setTextColor(oldColors);
                        mShareText.setPaintFlags(mShareText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            }
        });

        if (mMisPreferencias.getExport()) {
            mShareSwitch.setChecked(true);
        } else {
            mShareSwitch.setChecked(false);
        }

        if (mMisPreferencias.getImport()) {
            mImportSwitch.setChecked(true);
        } else {
            mImportSwitch.setChecked(false);
        }

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFile(TEXT_MIME_TYPE, String.valueOf(System.currentTimeMillis()).concat(TEXT_FILE_NAME));
            }
        });

        mShareSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShareSwitch.isChecked()) {
                    mMisPreferencias.setExport(true);
                } else {
                    mMisPreferencias.setExport(false);
                }
            }
        });


        mImportSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImportSwitch.isChecked()) {
                    mMisPreferencias.setImport(true);
                } else {
                    mMisPreferencias.setImport(false);
                }
            }
        });

        mImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

        ImportsAdapter limportsAdapter = new ImportsAdapter(mContext);
        limportsAdapter.addFeedbackListener((tipo, feedback, args) -> {
            switch (tipo) {
                case ImportsAdapter.TIPO_DELETE:
                    mImportablesViewModel.delete(((Importables) feedback).getUri());
                    break;
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mImportRecyclerView.setLayoutManager(layoutManager);
        mImportRecyclerView.setAdapter(limportsAdapter);

        LiveData<List<Importables>> limportables = mImportablesViewModel.getAllImportables();
        limportables.observe(getViewLifecycleOwner(), new Observer<List<Importables>>() {
            @Override
            public void onChanged(List<Importables> importables) {
                mLoadedImports = true;
                mImportablesList = importables;
                limportsAdapter.updateDataset(importables);
            }
        });

        mTextoMinutosIntervalo.setText(String.valueOf(mMisPreferencias.getMinutosIntervaloSync()));

        mRestaMinuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t = mMisPreferencias.getMinutosIntervaloSync();
                if (t > 1) {
                    mMisPreferencias.setMinutosIntervaloSync(t - 1);
                    mTextoMinutosIntervalo.setText(String.valueOf(t - 1));
                }
            }
        });

        mSumaMinuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int t = mMisPreferencias.getMinutosIntervaloSync();
                if (t < 10) {
                    mMisPreferencias.setMinutosIntervaloSync(t + 1);
                    mTextoMinutosIntervalo.setText(String.valueOf(t + 1));
                }
            }
        });

        mTextoProporcion.setText(String.valueOf(mMisPreferencias.getProporcionTrabajoOcio()));

        mProporcionMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = mMisPreferencias.getProporcionTrabajoOcio();
                if (p > 1) {
                    muestraDialogoMolesto(getActivity().getSupportFragmentManager(),
                            REQUEST_CODE_MENOS_PROPORCION,
                            mContext.getString(R.string.dialogo_titulo_reducir_proporcion),
                            mContext.getString(R.string.dialogo_mensaje_reducir_proporcion),
                            R.drawable.scale_balance);
                }
            }
        });

        mProporcionMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = mMisPreferencias.getProporcionTrabajoOcio();
                if (p < 10) {
                    mMisPreferencias.setProporcionTrabajoOcio(p + 1);
                    mTextoProporcion.setText(String.valueOf(p + 1));
                }
            }
        });

        mTextoAvisoRestantes.setText(String.valueOf(mMisPreferencias.getMinutosToast()));

        mMenosAvisoRestantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = mMisPreferencias.getMinutosToast();
                if (a > 0) {
                    mMisPreferencias.setMinutosToast(a - 1);
                    mTextoAvisoRestantes.setText(String.valueOf(a - 1));
                }
            }
        });

        mMasAvisoRestantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = mMisPreferencias.getMinutosToast();
                if (a < 60) {
                    mMisPreferencias.setMinutosToast(a + 1);
                    mTextoAvisoRestantes.setText(String.valueOf(a + 1));
                }
            }
        });

        if (mMisPreferencias.getTiempoDeGraciaActivado()) {
            mSwitchTiempoDeGracia.setText(R.string.espera_activada);
            mSwitchTiempoDeGracia.setChecked(true);
        } else {
            mSwitchTiempoDeGracia.setText(R.string.espera_desactivada);
            mSwitchTiempoDeGracia.setChecked(false);
        }

        mSwitchTiempoDeGracia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSwitchTiempoDeGracia.isChecked()) {
                    mSwitchTiempoDeGracia.setChecked(true);
                    muestraDialogoMolesto(getActivity().getSupportFragmentManager(),
                            REQUEST_CODE_DESACTIVA_TIEMPO_GRACIA,
                            mContext.getString(R.string.desactivar_el_tiempo_de_gracia_titulo_dialogo),
                            mContext.getString(R.string.tiempo_de_gracia_explicacion_dialogo),
                            R.drawable.clock_time_eight);
                } else {
                    mMisPreferencias.setTiempoDeGraciaActivado(true);
                    mSwitchTiempoDeGracia.setText(R.string.espera_activada);
                }
            }
        });

        if (mMisPreferencias.getAvisoCambioPositivaNegativaNeutral()) {
            mSwitchAvisoCambio.setText(R.string.si_avisar);
            mSwitchAvisoCambio.setChecked(true);
        } else {
            mSwitchAvisoCambio.setText(R.string.no_avisar);
            mSwitchAvisoCambio.setChecked(false);
        }

        mSwitchAvisoCambio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitchAvisoCambio.isChecked()){
                    mSwitchAvisoCambio.setText(R.string.si_avisar);
                    mMisPreferencias.setAvisoCambioPositivaNegativaNeutral(true);
                } else {
                    mSwitchAvisoCambio.setText(R.string.no_avisar);
                    mMisPreferencias.setAvisoCambioPositivaNegativaNeutral(false);
                }
            }
        });

        mToqueQuedaInicio.setText(mMisPreferencias.getInicioString());

        mToqueQuedaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestraDialogoMolesto(getActivity().getSupportFragmentManager(),
                        REQUEST_CODE_MOLESTO_TIME_INICIO,
                        mContext.getString(R.string.toque_de_queda_asecas),
                        mContext.getString(R.string.toque_de_queda_explicacion_dialogo),
                        R.drawable.police_badge);
            }
        });

        mToqueQuedaFin.setText(mMisPreferencias.getFinalString());

        mToqueQuedaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestraDialogoMolesto(getActivity().getSupportFragmentManager(),
                        REQUEST_CODE_MOLESTO_TIME_FIN,
                        mContext.getString(R.string.toque_de_queda_asecas),
                        mContext.getString(R.string.toque_de_queda_explicacion_dialogo),
                        R.drawable.police_badge);
            }
        });

        mTextToqueDeQuedaMinutosAviso.setText(String.valueOf(mMisPreferencias.getMinutosAvisoToqueDeQueda()));

        mButtonMenosToqueDeQuedaMinutosAviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int m = mMisPreferencias.getMinutosAvisoToqueDeQueda();
                if (m > 0) {
                    mTextToqueDeQuedaMinutosAviso.setText(String.valueOf(m-1));
                    mMisPreferencias.setMinutosAvisoToqueDeQueda(m-1);
                }
            }
        });

        mButtonMasToqueDeQuedaMinutosAviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int m = mMisPreferencias.getMinutosAvisoToqueDeQueda();
                if (m < 100) {
                    mTextToqueDeQuedaMinutosAviso.setText(String.valueOf(m+1));
                    mMisPreferencias.setMinutosAvisoToqueDeQueda(m+1);
                }
            }
        });

        if (mMisPreferencias.getAvisoToqueDeQuedaSiNo()){
            mSwitchAvisoToqueDeQueda.setChecked(true);
            mSwitchAvisoToqueDeQueda.setText(mContext.getString(R.string.si_avisar));
        } else {
            mSwitchAvisoToqueDeQueda.setChecked(false);
            mSwitchAvisoToqueDeQueda.setText(mContext.getString(R.string.no_avisar));
        }

        mSwitchAvisoToqueDeQueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitchAvisoToqueDeQueda.isChecked()){
                    mMisPreferencias.setAvisoToqueDeQuedaSiNo(true);
                    mSwitchAvisoToqueDeQueda.setText(R.string.si_avisar);
                } else {
                    mMisPreferencias.setAvisoToqueDeQuedaSiNo(false);
                    mSwitchAvisoToqueDeQueda.setText(R.string.no_avisar);
                }
                if (mMisPreferencias.getActivaToqueDeQuedaSiNo()) {
                    mSwitchAvisoToqueDeQueda.setEnabled(true);
                } else {
                    mSwitchAvisoToqueDeQueda.setEnabled(false);
                }
            }
        });

        if (mMisPreferencias.getActivaToqueDeQuedaSiNo()){
            mSwitchActivaToqueDeQueda.setChecked(true);
        } else {
            mSwitchActivaToqueDeQueda.setChecked(false);
            mSwitchAvisoToqueDeQueda.setEnabled(false);
        }
        mSwitchActivaToqueDeQueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSwitchActivaToqueDeQueda.isChecked()) {
                    mSwitchActivaToqueDeQueda.setChecked(true);
                    muestraDialogoMolesto(getActivity().getSupportFragmentManager(),
                            REQUEST_CODE_MOLESTO_ACTIVA_TOQUE,
                            mContext.getString(R.string.toque_de_queda_asecas),
                            mContext.getString(R.string.toque_de_queda_explicacion_dialogo),
                            R.drawable.police_badge);
                } else {
                    mSwitchAvisoToqueDeQueda.setEnabled(true);
                    mSwitchActivaToqueDeQueda.setText(R.string.activado);
                    mMisPreferencias.setActivaToqueDeQuedaSiNo(true);
                    viewDisabler.evaluateConditions();
                }
            }
        });

        mButtonPoliticaPrivacidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoliticaPrivacidadDialogo ppd = new PoliticaPrivacidadDialogo();
                ppd.show(getActivity().getSupportFragmentManager(), TAG_DIALOGO_POLITICA_PRIVACIDAD);
            }
        });

        if (mMisPreferencias.getNotifyConditionsNotMet()) {
            mSwitchNotifyConditionsNotMet.setText(R.string.si_avisar);
            mSwitchNotifyConditionsNotMet.setChecked(true);
        } else {
            mSwitchNotifyConditionsNotMet.setText(R.string.no_avisar);
            mSwitchNotifyConditionsNotMet.setChecked(false);
        }

        mSwitchNotifyConditionsNotMet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitchNotifyConditionsNotMet.isChecked()){
                    mSwitchNotifyConditionsNotMet.setText(R.string.si_avisar);
                    mMisPreferencias.setNotifyConditionsNotMet(true);
                } else {
                    mSwitchNotifyConditionsNotMet.setText(R.string.no_avisar);
                    mMisPreferencias.setNotifyConditionsNotMet(false);
                }
            }
        });

        if (mMisPreferencias.getNotifyConditionsJustMet()) {
            mSwitchNotifyConditionsRecentlyMet.setText(R.string.si_avisar);
            mSwitchNotifyConditionsRecentlyMet.setChecked(true);
        } else {
            mSwitchNotifyConditionsRecentlyMet.setText(R.string.no_avisar);
            mSwitchNotifyConditionsRecentlyMet.setChecked(false);
        }

        mSwitchNotifyConditionsRecentlyMet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwitchNotifyConditionsRecentlyMet.isChecked()) {
                    mSwitchNotifyConditionsRecentlyMet.setText(R.string.si_avisar);
                    mMisPreferencias.setNotifyConditionsJustMet(true);
                } else {
                    mSwitchNotifyConditionsRecentlyMet.setText(R.string.no_avisar);
                    mMisPreferencias.setNotifyConditionsJustMet(false);
                }
            }
        });

        mTextChangeOfDay.setText(String.valueOf(mMisPreferencias.getHourForChangeOfDay()));
        mButtonChangeOfDayMinus.setOnClickListener(view -> {
            int hour = mMisPreferencias.getHourForChangeOfDay();
            if (hour > 0) {
                hour --;
                mMisPreferencias.setHourForChangeOfDay(hour);
                mTextChangeOfDay.setText(String.valueOf(hour));
            }
        });
        mButtonChangeOfDayPlus.setOnClickListener(view -> {
            int hour = mMisPreferencias.getHourForChangeOfDay();
            if (hour < 6) {
                hour ++;
                mMisPreferencias.setHourForChangeOfDay(hour);
                mTextChangeOfDay.setText(String.valueOf(hour));
            }
        });

        if (mMisPreferencias.getNotifyChangeOfDay()) {
            mSwitchNotifyChangeOfDay.setChecked(true);
            mSwitchNotifyChangeOfDay.setText(R.string.si_avisar);
        } else {
            mSwitchNotifyChangeOfDay.setChecked(false);
            mSwitchNotifyChangeOfDay.setText(R.string.no_avisar);
        }
        mSwitchNotifyChangeOfDay.setOnClickListener(view -> {
            if (mSwitchNotifyChangeOfDay.isChecked()) {
                mSwitchNotifyChangeOfDay.setText(R.string.si_avisar);
                mMisPreferencias.setNotifyChangeOfDay(true);
            } else {
                mSwitchNotifyChangeOfDay.setText(R.string.no_avisar);
                mMisPreferencias.setNotifyChangeOfDay(false);
            }
        });

        mTextNotifyChangeOfDayMinutes.setText(String.valueOf(mMisPreferencias.getMinutesNotifyChangeOfDay()));
        mButtonNotifyChangeOfDayMinus.setOnClickListener(view -> {
            int minutes = mMisPreferencias.getMinutesNotifyChangeOfDay();
            if (minutes > 0) {
                minutes --;
                mMisPreferencias.setMinutesNotifyChangeOfDay(minutes);
                mTextNotifyChangeOfDayMinutes.setText(String.valueOf(minutes));
            }
        });
        mButtonNotifyChangeOfDayPlus.setOnClickListener(view -> {
            int minutes = mMisPreferencias.getMinutesNotifyChangeOfDay();
            if (minutes < 60) {
                minutes ++;
                mMisPreferencias.setMinutesNotifyChangeOfDay(minutes);
                mTextNotifyChangeOfDayMinutes.setText(String.valueOf(minutes));
            }
        });

        viewDisabler.evaluateConditions();
        return v;
    }

    /**
     *
     * End of onCreateView
     *
     */

    private void createFile(String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, REQUEST_CODE_WRITE);
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(TEXT_MIME_TYPE);
        startActivityForResult(intent, REQUEST_CODE_READ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_WRITE) {
                // Establecemos los flags para guardar los permisos que tenemos
                final int takeFlags = resultData.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (resultData != null) {
                    Uri uri = resultData.getData();
                    if (uri == null) {
                        return;
                    }
                    if (isInImports(uri.toString())){
                        toastYaEsta();
                        return;
                    }
                    // Tomar el permiso persistente para leer esta Uri
                    mContext.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    mLoadedExports = false;
                    ValueMap lvaluemap = new ValueMap(EXPORT_TXT_VALUE_MAP_KEY, uri.toString());
                    mValueMapViewModel.insert(lvaluemap);
                }
            } else if (requestCode == REQUEST_CODE_READ) {
                // Establecemos los flags para guardar los permisos que tenemos
                final int takeFlags = resultData.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (resultData != null) {
                    Uri uri = resultData.getData();
                    if (uri == null) {
                        return;
                    }
                    if (isInExports(uri.toString())){
                        toastYaEsta();
                        return;
                    }
                    // Tomar el permiso persistente para escribir a esta Uri
                    mContext.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    mLoadedImports = false;
                    Importables limportable = new Importables(uri.toString());
                    mImportablesViewModel.insert(limportable);
                }
            } else if (requestCode == REQUEST_CODE_MENOS_PROPORCION) {
                int valor = mMisPreferencias.getProporcionTrabajoOcio();
                mMisPreferencias.setProporcionTrabajoOcio(valor - 1);
                mTextoProporcion.setText(String.valueOf(valor - 1));
            } else if (requestCode == REQUEST_CODE_DESACTIVA_TIEMPO_GRACIA) {
                mMisPreferencias.setTiempoDeGraciaActivado(false);
                mSwitchTiempoDeGracia.setChecked(false);
                mSwitchTiempoDeGracia.setText(R.string.espera_desactivada);
            } else if (requestCode == REQUEST_CODE_TIME_PIQUER_INICIO || requestCode == REQUEST_CODE_TIME_PIQUER_FIN) {
                if (resultData != null) {
                    String txt = resultData.getStringExtra(TimePickerFragment.EXTRA_REPLY_STRING);
                    long hora = resultData.getIntExtra(TimePickerFragment.EXTRA_REPLY_HORA, -1);
                    long minuto = resultData.getIntExtra(TimePickerFragment.EXTRA_REPLY_MINUTO, -1);
                    if (txt != null) {
                        if (requestCode == REQUEST_CODE_TIME_PIQUER_INICIO) {
                            mToqueQuedaInicio.setText(txt);
                        } else if (requestCode == REQUEST_CODE_TIME_PIQUER_FIN) {
                            mToqueQuedaFin.setText(txt);
                        }
                    }
                    if (hora != -1 && minuto != -1) {
                        if (requestCode == REQUEST_CODE_TIME_PIQUER_INICIO) {
                            mMisPreferencias.setInicioToqueDeQueda(hora, minuto);
                        } else if (requestCode == REQUEST_CODE_TIME_PIQUER_FIN) {
                            mMisPreferencias.setFinalToqueDeQueda(hora, minuto);
                        }
                    }
                }
            } else if (requestCode == REQUEST_CODE_MOLESTO_TIME_INICIO) {
                muestraTimePickerDialog(true);
            } else if (requestCode == REQUEST_CODE_MOLESTO_TIME_FIN) {
                muestraTimePickerDialog(false);
            } else if (requestCode == REQUEST_CODE_MOLESTO_ACTIVA_TOQUE) {
                mSwitchActivaToqueDeQueda.setChecked(false);
                mSwitchActivaToqueDeQueda.setText(R.string.desactivado);
                mMisPreferencias.setActivaToqueDeQuedaSiNo(false);
                mSwitchAvisoToqueDeQueda.setEnabled(false);
                viewDisabler.evaluateConditions();
            }
        }
        if (mDialogDelayer != null) {
            mDialogDelayer.interrumpe();
        }
    }

    public void muestraDialogoMolesto(FragmentManager fm, int tipo, String titulo, String mensaje, int iconoResId) {
        if (titulo != null && mensaje != null && mContext.getDrawable(iconoResId) != null) {
            mDialogo = new DialogWithDelay(iconoResId, titulo, mensaje, 0);
            mDialogo.setTargetFragment(ConfiguracionFragment.this, tipo);
            mDialogo.show(fm, TAG_DIALOGO_CON_DELAY);
        }
    }

    public void muestraTimePickerDialog(boolean inicio) {
        DialogFragment newFragment = new TimePickerFragment();
        if (inicio) {
            newFragment.setTargetFragment(ConfiguracionFragment.this, REQUEST_CODE_TIME_PIQUER_INICIO);
        } else {
            newFragment.setTargetFragment(ConfiguracionFragment.this, REQUEST_CODE_TIME_PIQUER_FIN);
        }
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    private void toastYaEsta(){
        Toast toast = Toast.makeText(mContext, R.string.este_archivo_esta_asociado_a_otra_funcion, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean isInExports(String s){
        if (s.equals(Optional.ofNullable(mExportValueMap).map(vm -> vm.getValor()).orElse(null))){
            return true;
        }
        return false;
    }

    private boolean isInImports(String s){
        for (int i =0; i<mImportablesList.size(); i++){
            String uri = mImportablesList.get(i).getUri().toString();
            if (s.equals(uri)){
                return true;
            }
        }
        return false;
    }
}
