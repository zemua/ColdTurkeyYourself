package devs.mrp.coolyourturkey.watchdog;

public class WatchDogData {
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

    public boolean mUpdated;
    public WatchDogData setUpdated(boolean b) {
        this.mUpdated = b;
        return this;
    }
    public boolean ifUpdated() {
        return this.mUpdated;
    }
}
