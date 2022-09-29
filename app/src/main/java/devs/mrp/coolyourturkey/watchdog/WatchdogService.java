package devs.mrp.coolyourturkey.watchdog;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import devs.mrp.coolyourturkey.comun.GenericTimedToaster;
import devs.mrp.coolyourturkey.comun.MyBeanFactory;
import devs.mrp.coolyourturkey.comun.PermisosChecker;
import devs.mrp.coolyourturkey.comun.SingleExecutor;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.ToqueDeQuedaHandler;
import devs.mrp.coolyourturkey.databaseroom.EntryCleanerImpl;
import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListadaRepository;
import devs.mrp.coolyourturkey.databaseroom.valuemap.EntryCleaner;
import devs.mrp.coolyourturkey.grupos.conditionchecker.impl.ChangeCheckerFactory;
import devs.mrp.coolyourturkey.grupos.conditionchecker.impl.ConditionCheckerFactory;
import devs.mrp.coolyourturkey.grupos.conditionchecker.impl.GeneralConditionChecker;
import devs.mrp.coolyourturkey.grupos.packagemapper.impl.PackageConditionsCheckerFactory;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.export.TimeBlockExporter;
import devs.mrp.coolyourturkey.usagestats.ForegroundAppSpec;
import devs.mrp.coolyourturkey.watchdog.actionchain.AbstractHandler;
import devs.mrp.coolyourturkey.watchdog.checkscheduling.CheckManager;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;
import devs.mrp.coolyourturkey.watchdog.utils.Notifier;
import devs.mrp.coolyourturkey.watchdog.utils.impl.ChangeOfDayNotifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class WatchdogService extends LifecycleService {

    private static final String TAG = "WATCHDOG SERVICE TAG";
    private static final Object LOCK_0 = new Object();

    private final IBinder binder = new LocalBinder();

    SingleExecutor ejecutor;
    boolean ejecuta;
    NotificationManager mNotificationManager;
    ContadorRepository mContadorRepo;
    private Exporter mExporter;
    private Importer mImporter;
    private AbstractHandler actionRequestor;
    private CheckManager mCheckManager;
    private EntryCleaner mEntryCleaner;
    private Notifier changeOfDayNotifier;

    private WatchDogData mData;

    @Override
    public void onCreate() {
        super.onCreate();

        mExporter = new Exporter(this);
        mImporter = new Importer(this, this.getApplication());
        actionRequestor = MyBeanFactory.getActionRequestorFactory().getChainRequestor().getHandlerChain();
        mCheckManager = CheckManager.getInstance(this.getApplication(), this);
        mEntryCleaner = new EntryCleanerImpl(this.getApplication(), this, this);
        changeOfDayNotifier = new ChangeOfDayNotifier(this);

        mData = MyBeanFactory.getWatchDogDataFactory().create(this)
                .setSleepTime(1000 * 3) // 3 seconds between checks
                .setTimeLogHandler(new TimeLogHandler(this, this.getApplication(), this))
                .setProporcion(4)
                .setTimeDifferenceToUpdate(1000 * 60 * 1) // 1 minuto para actualizar el tiempo en la notificación
                .setWasPausado(true)
                .setTiempoImportado(0L)
                .setScreenBlock(new ScreenBlock(this, this.getApplication()))
                .setToquedeQuedaHandler(new ToqueDeQuedaHandler(this))
                .setMisPreferencias(new MisPreferencias(this))
                .setConditionToaster(new GenericTimedToaster(this.getApplication()))
                .setTimeBlockExporter(new TimeBlockExporter(this.getApplication(), this, this))
                .setChangeNotificationChecker(ChangeCheckerFactory.getChangeNotifier(this.getApplication(), this))
                .setConditionChecker(ConditionCheckerFactory.getConditionChecker(this.getApplication(), this))
                .setPackageConditionsChecker(PackageConditionsCheckerFactory.get(this.getApplication(), this));

        if (ejecutor == null) {
            ejecutor = new SingleExecutor();
        }
        setEjecuta(true);
        mData.setWatchDogHandler(new WatchdogHandler(this));

        mData.setNotification(mData.getWatchDogHandler().getNotificacionReposo());

        mData.getWatchDogHandler().registerOnOffBroadcast(this);
        // añadir listener para el broadcast encendido/apagado de pantalla
        mData.getWatchDogHandler().addFeedbackListener((FeedbackListener<Object>) (tipo, feedback, args) -> {
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
                    mData.setUltimoContador(new Contador(System.currentTimeMillis(), 0L));
                } else {
                    mData.setUltimoContador(contador.get(0));
                }
            }
        });

        mData.setTimePusher(MyBeanFactory.getTimePusherFactory().get(mContadorRepo));
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(WatchdogHandler.TURKEY_NOTIFICATION_ID, mData.getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        setEjecuta(true);

        if (!ejecutor.isExecuting()) { // si ya hay un servicio checkeando, no vamos
            initialize();
        }

        return START_STICKY;
    }

    private void initialize() {
        /**
         * the following are set before the "execute" because
         * livedata cannot be observed in a background thread
         */

        // Si reanudamos los checks después de estar parado, contar tiempo a partir de ahora
        mData.setLastEpoch(System.currentTimeMillis());

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
        mData.setForegroundAppChecker(lchecker);

        if (!ejecutor.isExecuting()) { // recheck nothing changed
            setupLoop(mData);
        }
    }

    private void setupLoop(WatchDogData data) {
        ejecutor.singleExecute(() -> {

            // referencia de la ultima notificacion y la que se mandaría ahora
            // para ver si actualizamos o mantenemos
            // para no mandar actalizaciones innecesarias
            int lultimanotif = ForegroundAppChecker.NULL;
            int lestanotif = ForegroundAppChecker.NULL;
            String lultimonombre = "";
            long lultimoAcumulado = 0L;
            boolean lupdated = false;

            data.setUltimaNotif(lultimanotif)
                    .setEstaNotif(lestanotif)
                    .setUltimoNombre(lultimonombre)
                    .setUltimoAcumulado(lultimoAcumulado)
                    .setUpdated(lupdated);

            while (getEjecuta()) {
                doLoopWork(data);
            }
        });
    }

    private void doLoopWork(WatchDogData data) {
        try {
            sleep(data.getSleepTime());

            data.getTimeLogHandler().watchDog();
            data.getChangeNotificationChecker().onChangedToMet(); // send notification if some groups changes to meet conditions
            data.getTimeBlockExporter().refresh();
            mEntryCleaner.cleanOlEntries();
            mCheckManager.refresh();
            changeOfDayNotifier.notify(null);

            if (PermisosChecker.checkPermisoEstadisticas(this)) {

                // tiempo transcurrido desde el último check
                data.setNow(System.currentTimeMillis());
                data.setMilisTranscurridos(data.getNow() - data.getLastEpoch());

                if (data.getMilisTranscurridos() < 0) {
                    data.setMilisTranscurridos(0);
                }
                data.setLastEpoch(data.getNow());
                data.setIsScreenOn(data.getWatchDogHandler().ifPhoneOnAndUnlocked());

                synchronized (LOCK_0) {
                    if (data.ifIsScreenOn() && ejecuta) {
                        // call to do stuff depending on the situation
                        doChoosenAction(data);
                        // close up this loop cycle and prepare for the next
                        closeUpCurrentLoopCycle(data);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doChoosenAction(WatchDogData data) {
        ForegroundAppSpec lapp = new ForegroundAppSpec();

        data.getForegroundAppChecker().getForegroundApp(lapp, data.getSleepTime());
        data.setPackageName(lapp.packageName);

        int ltipo = lapp.appType;
        data.setTiempoAcumulado(0L);
        data.setTiempoImportado(mImporter.importarTiempoTotal());
        //Log.d(TAG, "tiempo importado: " + String.valueOf(mTiempoImportado));
        data.setProporcion(data.getMisPreferencias().getProporcionTrabajoOcio());
        // fire up the chain to handle positive/negative/netrual app time
        actionRequestor.receiveRequest(ltipo, data);
    }

    private void closeUpCurrentLoopCycle(WatchDogData data) {
        // notice change positive/negative/neutral
        if (data.getEstaNotif() != data.getUltimanotif() && data.getMisPreferencias().getAvisoCambioPositivaNegativaNeutral()) {
            new TimeToaster(this.getApplication()).noticeChanged(data.getEstaNotif());
        }
        // update notification and data in exported files
        if (data.ifUpdated() || data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            data.setWasPausado(false);
            mExporter.export(data.getTiempoAcumulado());
            actualizaNotificacion(data.getNotification());
            data.setUltimoNombre(data.getPackageName());
            data.setUltimoAcumulado(data.getTiempoAcumulado());
            data.setUltimaNotif(data.getEstaNotif());
            data.setUpdated(false);
        }
        // check if we need to block
        data.getPackageConditionsChecker().onAllConditionsMet(data.getPackageName(), areMet -> {
            if (((data.getEstaNotif() == ForegroundAppChecker.NEGATIVO) && (data.getTiempoAcumulado() + data.getTiempoImportado() <= 0 || data.getToqueDeQuedaHandler().isToqueDeQueda() || !areMet))) {
                if (PermisosChecker.checkPermisoAlertas(this)) {
                    data.getScreenBlock().go();
                }
            }
        });
        data.getToqueDeQuedaHandler().avisar(); // notice for all kind of apps positive/negative/neutral
        // decrease points for not going to sleep
        if (data.getToqueDeQuedaHandler().isToqueDeQueda()) {
            if (data.getEstaNotif() != ForegroundAppChecker.NEGATIVO) { // if negative it is blocked + decreased before, if not and toque-de-queda true, it decreases points here
                negativeDecreaseCounter();
            }
        }
    }

    private void actualizaNotificacion(Notification n) {
        mNotificationManager.notify(WatchdogHandler.TURKEY_NOTIFICATION_ID, mData.getNotification());
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
        mData.getWatchDogHandler().unregisterOnOffBroadcast(this);
    }

    private void setEjecuta(boolean e) {
        synchronized (LOCK_0) {
            ejecuta = e;
            if (e == false) {
                if (mData.getUltimoContador() != null) {
                    mData.setProporcion(mData.getMisPreferencias().getProporcionTrabajoOcio());
                    mData.setNotification(mData.getWatchDogHandler().getNotificacionReposo(mData.getUltimoContador().getAcumulado() + mData.getTiempoImportado(), mData.getProporcion()));
                } else {
                    mData.setNotification(mData.getWatchDogHandler().getNotificacionReposo());
                }
                mNotificationManager.notify(WatchdogHandler.TURKEY_NOTIFICATION_ID, mData.getNotification());
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
        mData.setWasPausado(true);
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
        long lproporcionMilisTranscurridos = mData.getMilisTranscurridos() * mData.getProporcion();
        long lacumula = mData.getUltimoContador().getAcumulado() - lproporcionMilisTranscurridos;
        mData.getTimePusher().push(mData.getNow(), lacumula);
    }

    private void positiveIncreaseCounter() {
        if (!mData.getToqueDeQuedaHandler().isToqueDeQueda()) {
            long lacumula = mData.getUltimoContador().getAcumulado() + mData.getMilisTranscurridos();
            mData.getTimePusher().push(mData.getNow(), lacumula);
        }
    }
}
