package devs.mrp.coolyourturkey.configuracion;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import devs.mrp.coolyourturkey.R;

import java.util.Formatter;

public class MisPreferencias {

    private final String TAG = "MisPreferencias";

    private static final String FLAG_EXPORT = "flag.export.to.txt.activo";
    private static final String FLAG_IMPORT = "flag.import.from.txt.activo";
    private static final String MINUTOS_INTERVALO_SYNC = "minutos.sincronizacion.txt";
    private static final String PROPORCION_TRABAJO_OCIO = "minutos.trabajo.vs.minutos.ocio";
    private static final String MINUTOS_PARA_TOAST_AVISO = "minutos.para.avisar.con.toast";
    private static final String TIEMPO_MOLESTO_DE_GRACIA_ACTIVADO = "tiempo.molesto.de.gracia.activado";
    private static final String AVISO_CAMBIO_POSITIVA_NEGATIVA_NEUTRAL = "aviso.cambio.positiva.negativa.neutral";
    private static final String INICIO_TOQUE_DE_QUEDA = "inicio.tque.de.queda";
    private static final String FINAL_TOQUE_DE_QUEDA = "final.toque.de.queda";
    private static final String MINUTOS_AVISO_TOQUE_DE_QUEDA = "minutos.aviso.toque.de.queda";
    private static final String AVISO_TOQUE_DE_QUEDA_SI_NO = "aviso.toque.de.queda.si.no";
    private static final String ACTIVA_TOQUE_DE_QUEDA_SI_NO = "activa.toque.de.queda.si.no";
    private static final String WORK_PROFILE_IS_NEGATIVE_SI_NO = "work.profile.is.negative.si.no";
    private static final String BROADCAST_ON_OR_ELSE_RECEIVE = "broadcast.on.or.else.receive";
    private static final String NOTIFY_CONDITIONS_NOT_MET = "notify.conditions.not.met";
    private static final String NOTIFY_CONDITIONS_JUST_MET = "notify.conditions.just.met";
    private static final String NOTIFY_LIMITES_REACHED = "notify.limites.reached";
    private static final String HOUR_FOR_CHANGE_OF_DAY = "hour.for.change.of.day";
    private static final String NOTIFY_CHANGE_OF_DAY = "notify.change.of.day.switch";
    private static final String NOTIFY_CHANGE_OF_DAY_MINUTES = "notify.change.of.day.minutes";

    private static final String RANDOM_CHECK_CUSTOM_SOUND = "random.check.custom.sound";
    private static final String RANDOM_CHECK_TIMESTAMP = "random.check.time.stamp";

    Context mContext;
    private static SharedPreferences mSharedPreferences;

    public MisPreferencias(Context context) {
        mContext = context;
    }

    private SharedPreferences getSharedPreferences() {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.apk_cool_your_turkey_preference_file_key), Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    public void setExport(boolean b) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(FLAG_EXPORT, b);
        e.apply();
    }


    public boolean getExport() {
        return getSharedPreferences().getBoolean(FLAG_EXPORT, false);
    }

    public void setImport(boolean b) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(FLAG_IMPORT, b);
        e.apply();
    }

    public boolean getImport() {
        return getSharedPreferences().getBoolean(FLAG_IMPORT, false);
    }

    public void setMinutosIntervaloSync(int i) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putInt(MINUTOS_INTERVALO_SYNC, i);
        e.apply();
    }

    public int getMinutosIntervaloSync() {
        return getSharedPreferences().getInt(MINUTOS_INTERVALO_SYNC, 5);
    }

    public Long getMilisInterloSync() {
        return getMinutosIntervaloSync() * 60L * 1000L;
    }

    public void setProporcionTrabajoOcio(int i){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putInt(PROPORCION_TRABAJO_OCIO, i);
        e.apply();
    }

    public int getProporcionTrabajoOcio(){
        return getSharedPreferences().getInt(PROPORCION_TRABAJO_OCIO, 4);
    }

    public void setMinutosToast(int i){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putInt(MINUTOS_PARA_TOAST_AVISO, i);
        e.apply();
    }

    public int getMinutosToast(){
        return getSharedPreferences().getInt(MINUTOS_PARA_TOAST_AVISO, 10);
    }

    public long getMilisToast(){
        return getMinutosToast() * 60L * 1000L;
    }

    public void setTiempoDeGraciaActivado(boolean b){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(TIEMPO_MOLESTO_DE_GRACIA_ACTIVADO, b);
        e.apply();
    }

    public boolean getTiempoDeGraciaActivado(){
        return getSharedPreferences().getBoolean(TIEMPO_MOLESTO_DE_GRACIA_ACTIVADO, false);
    }

    public void setAvisoCambioPositivaNegativaNeutral(boolean b){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(AVISO_CAMBIO_POSITIVA_NEGATIVA_NEUTRAL, b);
        e.apply();
    }

    public boolean getAvisoCambioPositivaNegativaNeutral(){
        return getSharedPreferences().getBoolean(AVISO_CAMBIO_POSITIVA_NEGATIVA_NEUTRAL, false);
    }

    public void setInicioToqueDeQueda(long hora, long minuto){
        long milis = (hora*60*60*1000)+(minuto*60*1000);
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putLong(INICIO_TOQUE_DE_QUEDA, milis);
        e.apply();
    }

    public long getMilisInicioToqueDeQueda() {
        return getSharedPreferences().getLong(INICIO_TOQUE_DE_QUEDA, 0L);
    }

    public long getHoraInicioToqueDeQueda(long milis){
        return milis/(60*60*1000);
    }

    public long getHoraInicioToqueDeQueda() {
        long milis = getMilisInicioToqueDeQueda();
        return getHoraInicioToqueDeQueda(milis);
    }

    public long getMinutoInicioToqueDeQueda(long milis) {
        return (milis % (60 * 60 * 1000)) / (60 * 1000);
    }

    public long getMinutoInicioToqueDeQueda() {
        long milis = getMilisInicioToqueDeQueda();
        return getMinutoInicioToqueDeQueda(milis);
    }

    public String getInicioString(){
        Formatter f = new Formatter();
        long milis = getMilisInicioToqueDeQueda();
        long horas = getHoraInicioToqueDeQueda(milis);
        long minutos = getMinutoInicioToqueDeQueda(milis);
        f.format("%02d:%02d", horas, minutos);
        return f.toString();
    }

    public void setFinalToqueDeQueda(long hora, long minuto) {
        long milis = (hora*60*60*1000)+(minuto*60*1000);
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putLong(FINAL_TOQUE_DE_QUEDA, milis);
        e.apply();
    }

    public long getMilisFinalToqueDeQueda() {
        return getSharedPreferences().getLong(FINAL_TOQUE_DE_QUEDA, 0L);
    }

    public long getHoraFinalToqueDeQueda(long milis) {
        return milis/(60*60*1000);
    }

    public long getHoraFinalToqueDeQueda() {
        long milis = getMilisFinalToqueDeQueda();
        return getHoraFinalToqueDeQueda(milis);
    }

    public long getMinutoFinalToqueDeQueda(long milis) {
        return (milis % (60 * 60 * 1000)) / (60 * 1000);
    }

    public long getMinutoFinalToqueDeQueda() {
        long milis = getMilisFinalToqueDeQueda();
        return getMinutoFinalToqueDeQueda(milis);
    }

    public String getFinalString(){
        Formatter f = new Formatter();
        long milis = getMilisFinalToqueDeQueda();
        long horas = getHoraFinalToqueDeQueda(milis);
        long minutos = getMinutoFinalToqueDeQueda(milis);
        f.format("%02d:%02d", horas, minutos);
        return f.toString();
    }

    public void setMinutosAvisoToqueDeQueda(int i){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putInt(MINUTOS_AVISO_TOQUE_DE_QUEDA, i);
        e.apply();
    }

    public int getMinutosAvisoToqueDeQueda(){
        return getSharedPreferences().getInt(MINUTOS_AVISO_TOQUE_DE_QUEDA, 10);
    }

    public void setAvisoToqueDeQuedaSiNo(boolean b){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(AVISO_TOQUE_DE_QUEDA_SI_NO, b);
        e.apply();
    }

    public boolean getAvisoToqueDeQuedaSiNo(){
        return getSharedPreferences().getBoolean(AVISO_TOQUE_DE_QUEDA_SI_NO, false);
    }

    public void setActivaToqueDeQuedaSiNo(boolean b){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(ACTIVA_TOQUE_DE_QUEDA_SI_NO, b);
        e.apply();
    }

    public boolean getActivaToqueDeQuedaSiNo(){
        return getSharedPreferences().getBoolean(ACTIVA_TOQUE_DE_QUEDA_SI_NO, false);
    }

    public void setWorkProfileNegative(boolean b) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(WORK_PROFILE_IS_NEGATIVE_SI_NO, b);
        e.apply();
    }

    public boolean getWorkProfileNegative() {
        return getSharedPreferences().getBoolean(WORK_PROFILE_IS_NEGATIVE_SI_NO, false);
    }

    public void setBroadcastOn(boolean b){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(BROADCAST_ON_OR_ELSE_RECEIVE, b);
        e.apply();
    }

    public boolean getBroadcastOn(){
        return getSharedPreferences().getBoolean(BROADCAST_ON_OR_ELSE_RECEIVE, false);
    }

    public void setNotifyConditionsNotMet(boolean b){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(NOTIFY_CONDITIONS_NOT_MET, b);
        e.apply();
    }

    public boolean getNotifyConditionsNotMet(){
        return getSharedPreferences().getBoolean(NOTIFY_CONDITIONS_NOT_MET, false);
    }

    public void setNotifyConditionsJustMet(boolean b) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(NOTIFY_CONDITIONS_JUST_MET, b);
        e.apply();
    }

    public boolean getNotifyConditionsJustMet() {
        return getSharedPreferences().getBoolean(NOTIFY_CONDITIONS_JUST_MET, true);
    }

    public void setNotifyLimitesReached(boolean b) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(NOTIFY_LIMITES_REACHED, b);
        e.apply();
    }

    public boolean getNotifyLimitesReached() {
        return getSharedPreferences().getBoolean(NOTIFY_LIMITES_REACHED, true);
    }

    public void setRandomCheckSound(Uri uri){
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putString(RANDOM_CHECK_CUSTOM_SOUND, uri.getPath());
        e.apply();
    }

    public Uri getRandomCheckSound(){
        String s = getSharedPreferences().getString(RANDOM_CHECK_CUSTOM_SOUND, "");
        Uri uri;
        if (s.equals("")) {
            uri = Settings.System.DEFAULT_NOTIFICATION_URI;
        } else {
            uri = Uri.parse(s);
        }
        //Log.d(TAG, "sound uri: " + uri.toString());
        return uri;
    }

    public void setLastRandomCheckTimeStamp(long time) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putLong(RANDOM_CHECK_TIMESTAMP, time);
        e.apply();
    }

    public long getLastRandomCheckTimeStamp() {
        long l = getSharedPreferences().getLong(RANDOM_CHECK_TIMESTAMP, 0L);
        return l;
    }

    public void setHourForChangeOfDay(int hour) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putInt(HOUR_FOR_CHANGE_OF_DAY, hour);
        e.apply();
    }

    public int getHourForChangeOfDay() {
        int i = getSharedPreferences().getInt(HOUR_FOR_CHANGE_OF_DAY, 3);
        return i;
    }

    public void setNotifyChangeOfDay(boolean notify) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putBoolean(NOTIFY_CHANGE_OF_DAY, notify);
        e.apply();
    }

    public boolean getNotifyChangeOfDay() {
        boolean i = getSharedPreferences().getBoolean(NOTIFY_CHANGE_OF_DAY, true);
        return i;
    }

    public void setMinutesNotifyChangeOfDay(int minutes) {
        SharedPreferences.Editor e = getSharedPreferences().edit();
        e.putInt(NOTIFY_CHANGE_OF_DAY_MINUTES, minutes);
        e.apply();
    }

    public int getMinutesNotifyChangeOfDay() {
        int i = getSharedPreferences().getInt(NOTIFY_CHANGE_OF_DAY_MINUTES, 10);
        return i;
    }

}
