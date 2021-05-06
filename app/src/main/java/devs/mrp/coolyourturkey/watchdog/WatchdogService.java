package devs.mrp.coolyourturkey.watchdog;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.GenericTimedToaster;
import devs.mrp.coolyourturkey.comun.PermisosChecker;
import devs.mrp.coolyourturkey.comun.SingleExecutor;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.ToqueDeQuedaHandler;
import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaRepository;
import devs.mrp.coolyourturkey.databaseroom.valuemap.ValueMapRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.usagestats.ForegroundAppSpec;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;
import devs.mrp.coolyourturkey.workspace.TurkeyBroadcaster;
import devs.mrp.coolyourturkey.workspace.TurkeyBroadreader;
import devs.mrp.coolyourturkey.workspace.WspController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class WatchdogService extends LifecycleService {

    // TODO simplify the loop and decentralize in other classes
    // TODO periodically do some cleanup of old registers of ContadorRepository

    private static final String TAG = "WATCHDOG SERVICE TAG";
    private static final Object LOCK_0 = new Object();

    private final IBinder binder = new LocalBinder();

    private final long TIME_DIFF_TO_UPDATE = 1000 * 60 * 1; // 1 minuto para actualizar tiempo en notificacion
    private static long sProporcion = 4;

    SingleExecutor ejecutor;
    boolean ejecuta;
    long sleeptime;
    boolean wasPausado = true;
    WatchdogHandler mHandler;
    Notification mNotificacion;
    NotificationManager mNotificationManager;
    long lastEpoch;
    long milisTranscurridos;
    long now;
    ContadorRepository mContadorRepo;
    Contador mUltimoContador;
    private GenericTimedToaster mConditionToaster;
    private Exporter mExporter;
    private Importer mImporter;
    private Long mTiempoImportado;
    ScreenBlock mScreenBlock;
    private MisPreferencias mMisPreferencias;
    private ToqueDeQuedaHandler mToqueDeQuedaHandler;
    private TimeLogHandler mTimeLogHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mScreenBlock = new ScreenBlock(this);
        wasPausado = true;
        mExporter = new Exporter(this);
        mImporter = new Importer(this, this.getApplication());
        mTiempoImportado = 0L;
        mMisPreferencias = new MisPreferencias(this);
        mToqueDeQuedaHandler = new ToqueDeQuedaHandler(this);
        mTimeLogHandler = new TimeLogHandler(this, this.getApplication(), this);
        mConditionToaster = new GenericTimedToaster(this.getApplication());

        if (ejecutor == null) {
            ejecutor = new SingleExecutor();
        }
        setEjecuta(true);
        mHandler = new WatchdogHandler(this);

        mNotificacion = mHandler.getNotificacionReposo();

        mHandler.registerOnOffBroadcast(this);
        // añadir listener para el broadcast encendido/apagado de pantalla
        mHandler.addFeedbackListener((FeedbackListener<Object>) (tipo, feedback, args) -> {
            if (tipo == WatchdogHandler.FEEDBACK_APAGADA) {
                pausar();
            } else if (tipo == WatchdogHandler.FEEDBACK_ENCENDIDA) {
                WatchdogStarter lstarter = new WatchdogStarter(this);
                lstarter.startService();
            }
        });

        // Observer del último contador para saber cual es el último tiempo acumulado que tenemos
        mContadorRepo = ContadorRepository.getRepo(this.getApplication());
        mContadorRepo.getUltimoContador().observe(this, new Observer<List<Contador>>() {
            @Override
            public void onChanged(List<Contador> contador) {
                if (contador.size() == 0) {
                    // no hay registros
                    mUltimoContador = new Contador(System.currentTimeMillis(), 0L);
                } else {
                    mUltimoContador = contador.get(0);
                }
            }
        });

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(WatchdogHandler.TURKEY_NOTIFICATION_ID, mNotificacion);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        setEjecuta(true);

        if (!ejecutor.isExecuting()) { // si ya hay un servicio checkeando, no vamos

            /**
             * the following are set before the "execute" because
             * livedata cannot be observed in a background thread
             */

            // Si reanudamos los checks después de estar parado, contar tiempo a partir de ahora
            lastEpoch = System.currentTimeMillis();

            LiveData<Map<String, AplicacionListada>> lGoodMap;
            LiveData<Map<String, AplicacionListada>> lBadMap;
            ForegroundAppChecker lchecker;

            AplicacionListadaRepository lRepo = AplicacionListadaRepository.getRepo(getApplication());
            LiveData<List<AplicacionListada>> lnegativas = lRepo.getAPPsNegativas();
            lBadMap = Transformations.map(lnegativas, this::listToMap);
            LiveData<List<AplicacionListada>> lpositivas = lRepo.getAppsPositivas();
            lGoodMap = Transformations.map(lpositivas, this::listToMap);

            lchecker = new ForegroundAppChecker(this, lGoodMap.getValue(), lBadMap.getValue());

            lBadMap.observe(this, new Observer<Map<String, AplicacionListada>>() {
                @Override
                public void onChanged(Map<String, AplicacionListada> aplicacionListadas) {
                    lchecker.actualizaMalas(aplicacionListadas);
                }
            });

            lGoodMap.observe(this, new Observer<Map<String, AplicacionListada>>() {
                @Override
                public void onChanged(Map<String, AplicacionListada> aplicacionListadas) {
                    lchecker.actualizaBuenas(aplicacionListadas);
                }
            });

            if (!ejecutor.isExecuting()) { // recheck nothing changed

                ejecutor.singleExecute(() -> {

                    // referencia de la ultima notificacion y la que se mandaría ahora
                    // para ver si actualizamos o mantenemos
                    // para no mandar actalizaciones innecesarias
                    int lultimanotif = ForegroundAppChecker.NULL;
                    int lestanotif = ForegroundAppChecker.NULL;
                    String lultimonombre = "";
                    long lultimoAcumulado = 0L;
                    boolean lupdated = false;

                    while (getEjecuta()) {
                        try {
                            sleeptime = 1000 * 3; // 3 seconds between checks
                            sleep(sleeptime);

                            mTimeLogHandler.watchDog(); // perform periodic stuff in the handler

                            if (PermisosChecker.checkPermisoEstadisticas(this)) {

                                // tiempo transcurrido desde el último check
                                now = System.currentTimeMillis();
                                milisTranscurridos = now - lastEpoch;

                                if (milisTranscurridos < 0) {
                                    milisTranscurridos = 0;
                                }
                                lastEpoch = now;
                                boolean isScreenOn = mHandler.ifPhoneOnAndUnlocked();

                                synchronized (LOCK_0) {
                                    if (isScreenOn && ejecuta) {
                                        ForegroundAppSpec lapp = new ForegroundAppSpec();
                                        String lnombre;

                                        lchecker.getForegroundApp(lapp, sleeptime);
                                        lnombre = lapp.packageName;

                                        int ltipo = lapp.appType;
                                        long lacumula = 0L;
                                        mTiempoImportado = mImporter.importarTiempoTotal();
                                        //Log.d(TAG, "tiempo importado: " + String.valueOf(mTiempoImportado));
                                        sProporcion = mMisPreferencias.getProporcionTrabajoOcio();
                                        switch (ltipo) {
                                            case ForegroundAppChecker.NEGATIVO:
                                                lestanotif = ForegroundAppChecker.NEGATIVO;
                                                if (mUltimoContador != null) {
                                                    long lproporcionMilisTranscurridos = milisTranscurridos * sProporcion;
                                                    lacumula = mUltimoContador.getAcumulado() - lproporcionMilisTranscurridos;
                                                }
                                                if (lestanotif != lultimanotif || !lultimonombre.equals(lnombre) || Math.abs(lacumula - lultimoAcumulado) > TIME_DIFF_TO_UPDATE || wasPausado) {
                                                    mNotificacion = mHandler.getNotificacionNegativa(lnombre, lacumula + mTiempoImportado, sProporcion);
                                                    lupdated = true;
                                                } else {
                                                    lupdated = false;
                                                }
                                                if (!mScreenBlock.estamosBloqueando()) {
                                                    pushAcumulado(now, lacumula);
                                                    try {mTimeLogHandler.insertTimeBadApp(lnombre, milisTranscurridos);} catch (Exception e) {e.printStackTrace();}
                                                }
                                                new TimeToaster(this.getApplication()).noticeTimeLeft((lacumula + mTiempoImportado) / sProporcion);
                                                break;
                                            case ForegroundAppChecker.NEUTRO:
                                                lestanotif = ForegroundAppChecker.NEUTRO;
                                                lacumula = mUltimoContador.getAcumulado();
                                                if (lestanotif != lultimanotif || !lultimonombre.equals(lnombre) || Math.abs(lacumula - lultimoAcumulado) > TIME_DIFF_TO_UPDATE || wasPausado) {
                                                    mNotificacion = mHandler.getNotificacionNeutra(lnombre, lacumula + mTiempoImportado, sProporcion);
                                                    lupdated = true;
                                                } else {
                                                    lupdated = false;
                                                }
                                                try {mTimeLogHandler.insertTimeNeutralApp(lnombre, milisTranscurridos);} catch (Exception e) {e.printStackTrace();}
                                                break;
                                            case ForegroundAppChecker.NULL:
                                                lestanotif = ForegroundAppChecker.NULL;
                                                lacumula = mUltimoContador.getAcumulado();
                                                if (lestanotif != lultimanotif || !lultimonombre.equals(lnombre) || Math.abs(lacumula - lultimoAcumulado) > TIME_DIFF_TO_UPDATE || wasPausado) {
                                                    mNotificacion = mHandler.getNotificacionNeutra(lnombre, lacumula + mTiempoImportado, sProporcion);
                                                    lupdated = true;
                                                } else {
                                                    lupdated = false;
                                                }
                                                // no log this time because we don't know which kind of app is it
                                                break;
                                            case ForegroundAppChecker.POSITIVO:
                                                lestanotif = ForegroundAppChecker.POSITIVO;
                                                if (mUltimoContador != null) {
                                                    lacumula = mUltimoContador.getAcumulado() + milisTranscurridos;
                                                }
                                                mNotificacion = mHandler.getNotificacionPositiva(mTimeLogHandler, lnombre, lacumula + mTiempoImportado, sProporcion);
                                                lupdated = true;
                                                if (!mToqueDeQuedaHandler.isToqueDeQueda()) {
                                                    if (mTimeLogHandler.ifAllAppConditionsMet(lnombre) && !mTimeLogHandler.ifLimitsReachedForAppName(lnombre)) {
                                                        pushAcumulado(now, lacumula);
                                                    } else {
                                                        if (mMisPreferencias.getNotifyConditionsNotMet() && !mTimeLogHandler.ifAllAppConditionsMet(lnombre)) {mConditionToaster.noticeMessage(this.getApplication().getResources().getString(R.string.conditions_not_met));}
                                                        else if (mMisPreferencias.getNotifyLimitesReached() && mTimeLogHandler.ifLimitsReachedForAppName(lnombre)) {mConditionToaster.noticeMessage(this.getApplication().getResources().getString(R.string.has_alcanzado_el_limite_de_puntos));}
                                                    }
                                                    try {mTimeLogHandler.insertTimeGoodApp(lnombre, milisTranscurridos);} catch (Exception e) {e.printStackTrace();}
                                                }
                                                break;
                                        }

                                        if (lestanotif != lultimanotif && mMisPreferencias.getAvisoCambioPositivaNegativaNeutral()) {
                                            new TimeToaster(this.getApplication()).noticeChanged(lestanotif);
                                        }
                                        if (lupdated) {
                                            wasPausado = false;
                                            mExporter.export(lacumula);
                                            actualizaNotificacion(mNotificacion);
                                            lultimonombre = lnombre;
                                            lultimoAcumulado = lacumula;
                                            lultimanotif = lestanotif;
                                            lupdated = false;
                                        }
                                        if ((lestanotif == ForegroundAppChecker.NEGATIVO) && (lacumula + mTiempoImportado <= 0 || mToqueDeQuedaHandler.isToqueDeQueda())) {
                                            if (PermisosChecker.checkPermisoAlertas(this)) {
                                                mScreenBlock.go();
                                            }
                                        } else {
                                            mScreenBlock.desbloquear();
                                        }
                                        mToqueDeQuedaHandler.avisar(); // notice for all kind of apps positive/negative/neutral
                                        if (mToqueDeQuedaHandler.isToqueDeQueda()) {
                                            if (lestanotif != ForegroundAppChecker.NEGATIVO) {
                                                // if negative it is blocked + decreased before, if not and toque-de-queda true, it decreases points here
                                                negativeDecreaseCounter();
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        return START_STICKY;
    }

    private void pushAcumulado(long epoch, long acumulado) {
        Contador lcontador = new Contador(epoch, acumulado);
        mContadorRepo.insert(lcontador);
    }

    private void actualizaNotificacion(Notification n) {
        mNotificationManager.notify(WatchdogHandler.TURKEY_NOTIFICATION_ID, mNotificacion);
    }

    private Map<String, AplicacionListada> listToMap(List<AplicacionListada> lista) {
        Map<String, AplicacionListada> lmapa = new HashMap<>();
        lista.forEach((app) -> {
            lmapa.put(app.getNombre(), app);
        });
        return lmapa;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flagsOff();
        mHandler.unregisterOnOffBroadcast(this);
        mScreenBlock.desbloquear();
    }

    private void setEjecuta(boolean e) {
        synchronized (LOCK_0) {
            ejecuta = e;
            if (e == false) {
                if (mUltimoContador != null) {
                    sProporcion = mMisPreferencias.getProporcionTrabajoOcio();
                    mNotificacion = mHandler.getNotificacionReposo(mUltimoContador.getAcumulado() + mTiempoImportado, sProporcion);
                } else {
                    mNotificacion = mHandler.getNotificacionReposo();
                }
                mNotificationManager.notify(WatchdogHandler.TURKEY_NOTIFICATION_ID, mNotificacion);
            }
        }
    }

    private boolean getEjecuta() {
        synchronized (LOCK_0) {
            return ejecuta;
        }
    }

    public void pausar() {
        setEjecuta(false);
        wasPausado = true;
    }

    private void flagsOff() {
        setEjecuta(false);
        ejecutor.stop();
    }

    public class LocalBinder extends Binder {
        public WatchdogService getService() {
            return WatchdogService.this;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return binder;
    }

    private void negativeDecreaseCounter(){
        long lproporcionMilisTranscurridos = milisTranscurridos * sProporcion;
        long lacumula = mUltimoContador.getAcumulado() - lproporcionMilisTranscurridos;
        pushAcumulado(now, lacumula);
    }

    private void positiveIncreaseCounter() {
        if (!mToqueDeQuedaHandler.isToqueDeQueda()) {
            long lacumula = mUltimoContador.getAcumulado() + milisTranscurridos;
            pushAcumulado(now, lacumula);
        }
    }
}
