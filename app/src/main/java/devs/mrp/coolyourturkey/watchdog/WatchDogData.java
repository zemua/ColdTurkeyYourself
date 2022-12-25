package devs.mrp.coolyourturkey.watchdog;

import android.app.Notification;

import devs.mrp.coolyourturkey.comun.GenericTimedToaster;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.ToqueDeQuedaHandler;
import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ChangeChecker;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageConditionsChecker;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class WatchDogData {

    private final WatchdogService mService;
    public WatchDogData(WatchdogService service) {
        this.mService = service;
    }

    public WatchdogService getService() {
        return this.mService;
    }

    private ForegroundAppChecker lchecker;
    public WatchDogData setForegroundAppChecker(ForegroundAppChecker checker){
        this.lchecker = checker;
        return this;
    }
    public ForegroundAppChecker getForegroundAppChecker() {
        return lchecker;
    }

    private int mUltimanotif;
    public WatchDogData setUltimaNotif(int ultimaNotif){
        this.mUltimanotif = ultimaNotif;
        return this;
    }
    public int getUltimanotif() {
        return mUltimanotif;
    }

    private int mEstaNotif;
    public WatchDogData setEstaNotif(int estaNotif) {
        this.mEstaNotif = estaNotif;
        return this;
    }
    public int getEstaNotif() {
        return this.mEstaNotif;
    }

    private String mUltimoNombre;
    public WatchDogData setUltimoNombre(String s){
        this.mUltimoNombre = s;
        return this;
    }
    public String getUltimoNombre() {
        return mUltimoNombre;
    }

    private long mUltimoAcumulado;
    public WatchDogData setUltimoAcumulado(long l) {
        this.mUltimoAcumulado = l;
        return this;
    }
    public long getUltimoAcumulado() {
        return mUltimoAcumulado;
    }

    private boolean mUpdated;
    public WatchDogData setUpdated(boolean b) {
        this.mUpdated = b;
        return this;
    }
    public boolean ifUpdated() {
        return this.mUpdated;
    }

    private long mMilisTranscurridos;
    public WatchDogData setMilisTranscurridos(long milis) {
        this.mMilisTranscurridos = milis;
        return this;
    }
    public long getMilisTranscurridos() {
        return mMilisTranscurridos;
    }

    private long mSleepTime;
    public WatchDogData setSleepTime(long milis) {
        this.mSleepTime = milis;
        return this;
    }
    public long getSleepTime() {
        return this.mSleepTime;
    }

    private TimeLogHandler mTimeLogHandler;
    public WatchDogData setTimeLogHandler(TimeLogHandler handler) {
        this.mTimeLogHandler = handler;
        return this;
    }
    public TimeLogHandler getTimeLogHandler() {
        return mTimeLogHandler;
    }

    private long mNow;
    public WatchDogData setNow(long milis) {
        this.mNow = milis;
        return this;
    }
    public long getNow() {
        return mNow;
    }

    private long mLastEpoch;
    public WatchDogData setLastEpoch(long milis) {
        this.mLastEpoch = milis;
        return this;
    }
    public long getLastEpoch() {
        return mLastEpoch;
    }

    private boolean mIsScreenOn;
    public WatchDogData setIsScreenOn(boolean b) {
        this.mIsScreenOn = b;
        return this;
    }
    public boolean ifIsScreenOn() {
        return this.mIsScreenOn;
    }

    private WatchdogHandler mWatchDogHandler;
    public WatchDogData setWatchDogHandler(WatchdogHandler handler) {
        this.mWatchDogHandler = handler;
        return this;
    }
    public WatchdogHandler getWatchDogHandler() {
        return mWatchDogHandler;
    }

    private String mPackageName;
    public WatchDogData setPackageName(String name) {
        this.mPackageName = name;
        return this;
    }
    public String getPackageName() {
        return this.mPackageName;
    }

    private long mTiempoAcumulado;
    public WatchDogData setTiempoAcumulado(long milis) {
        this.mTiempoAcumulado = milis;
        return this;
    }
    public long getTiempoAcumulado() {
        return mTiempoAcumulado;
    }

    private Contador mUltimoContador;
    public WatchDogData setUltimoContador(Contador contador) {
        this.mUltimoContador = contador;
        return this;
    }
    public Contador getUltimoContador() {
        return mUltimoContador;
    }

    private static long mProporcion;
    public WatchDogData setProporcion(long proporcion) {
        this.mProporcion = proporcion;
        return this;
    }
    public long getProporcion() {
        return this.mProporcion;
    }

    private static long mTimeDifferenceToUpdate;
    public WatchDogData setTimeDifferenceToUpdate(long milis) {
        mTimeDifferenceToUpdate = milis;
        return this;
    }
    public long getTimeDifferenceToUpdate() {
        return mTimeDifferenceToUpdate;
    }

    private boolean mWasPausado;
    public WatchDogData setWasPausado(boolean b) {
        this.mWasPausado = b;
        return this;
    }
    public boolean getWasPausado() {
        return this.mWasPausado;
    }

    private Notification mNotification;
    public WatchDogData setNotification(Notification notification) {
        this.mNotification = notification;
        return this;
    }
    public Notification getNotification() {
        return mNotification;
    }

    private long mTiempoImportado;
    public WatchDogData setTiempoImportado(long milis) {
        this.mTiempoImportado = milis;
        return this;
    }
    public long getTiempoImportado() {
        return this.mTiempoImportado;
    }

    private ScreenBlock mScreenBlock;
    public WatchDogData setScreenBlock(ScreenBlock block){
        this.mScreenBlock = block;
        return this;
    }
    public ScreenBlock getScreenBlock() {
        return this.mScreenBlock;
    }

    private TimePusherInterface mTimePusher;
    public WatchDogData setTimePusher(TimePusherInterface pusher) {
        this.mTimePusher = pusher;
        return this;
    }
    public TimePusherInterface getTimePusher() {
        return this.mTimePusher;
    }

    private ToqueDeQuedaHandler mToqueDeQuedaHandler;
    public WatchDogData setToquedeQuedaHandler(ToqueDeQuedaHandler handler) {
        this.mToqueDeQuedaHandler = handler;
        return this;
    }
    public ToqueDeQuedaHandler getToqueDeQuedaHandler() {
        return this.mToqueDeQuedaHandler;
    }

    private MisPreferencias mMisPreferencias;
    public WatchDogData setMisPreferencias(MisPreferencias preferencias) {
        this.mMisPreferencias = preferencias;
        return this;
    }
    public MisPreferencias getMisPreferencias() {
        return this.mMisPreferencias;
    }

    private GenericTimedToaster mGenericTimedToaster;
    public WatchDogData setConditionToaster(GenericTimedToaster toaster) {
        this.mGenericTimedToaster = toaster;
        return this;
    }
    public GenericTimedToaster getConditionToaster() {
        return this.mGenericTimedToaster;
    }

    private ChangeChecker changeNotificationChecker;
    public WatchDogData setChangeNotificationChecker(ChangeChecker checker) {
        this.changeNotificationChecker = checker;
        return this;
    }
    public ChangeChecker getChangeNotificationChecker() {
        return this.changeNotificationChecker;
    }

    private ConditionCheckerCommander conditionCheckerCommander;
    public WatchDogData setConditionChecker(ConditionCheckerCommander checker) {
        this.conditionCheckerCommander = checker;
        return this;
    }
    public ConditionCheckerCommander getConditionCheckerCommander() {
        return this.conditionCheckerCommander;
    }

    private PackageConditionsChecker packageConditionsChecker;
    public WatchDogData setPackageConditionsChecker(PackageConditionsChecker checker) {
        this.packageConditionsChecker = checker;
        return this;
    }
    public PackageConditionsChecker getPackageConditionsChecker() {
        return this.packageConditionsChecker;
    }
}
