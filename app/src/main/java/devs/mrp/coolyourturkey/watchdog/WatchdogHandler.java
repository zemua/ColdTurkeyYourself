package devs.mrp.coolyourturkey.watchdog;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import devs.mrp.coolyourturkey.MainActivity;
import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.configuracion.ToqueDeQuedaHandler;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class WatchdogHandler implements Feedbacker<Object> {

    private static final String TAG = "WATCH DOG HANDLER";

    public static final int TURKEY_NOTIFICATION_ID = 100;
    public static final String CHANNEL_ID = "coldturkeyyourself_service_channel";
    public static final int WATCHDOG_SERVICE_INTENT_ID = 1;
    public static final String WATCHDOG_ACTIVO_DB_ID = "wd_activo";

    public static final int FEEDBACK_ENCENDIDA = 0;
    public static final int FEEDBACK_APAGADA = 1;
    private List<FeedbackListener<Object>> feedbackList = new ArrayList<>();

    Intent mNotificationIntent;
    Notification mNotificacion;
    PendingIntent mPendingIntent;
    Context mContext;
    NotificationChannel serviceChannel;
    WatchdogScreenOnOffReceiver br;
    PackageManager mPackageManager;
    private ToqueDeQuedaHandler mToqueDeQuedaHandler;

    WatchdogHandler(Context context) {
        mContext = context;
        mNotificationIntent = new Intent(context, MainActivity.class);

        mPackageManager = context.getPackageManager();

        mPendingIntent = PendingIntent.getActivity(context, WATCHDOG_SERVICE_INTENT_ID, mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mToqueDeQuedaHandler = new ToqueDeQuedaHandler(context);
    }

    public Notification getNotificacionReposo() {
        // set standard notification
        createNotificationChannel();
        mNotificacion = new Notification.Builder(mContext, CHANNEL_ID)
                .setContentTitle(mContext.getText(R.string.titulo_notificacion_servicio))
                .setContentText(mContext.getText(R.string.content_notificacion_servicio))
                .setSmallIcon(R.drawable.notificacion_sleep)
                .setContentIntent(mPendingIntent)
                .build();
        return mNotificacion;
    }

    public Notification getNotificacionReposo(Long restante, Long proporcion) {
        // set standard notification
        createNotificationChannel();
        mNotificacion = new Notification.Builder(mContext, CHANNEL_ID)
                .setContentTitle(mContext.getText(R.string.titulo_notificacion_servicio))
                .setContentText(mContext.getText(R.string.content_notificacion_servicio) + milisToTime(restante / proporcion))
                .setSmallIcon(R.drawable.notificacion_sleep)
                .setContentIntent(mPendingIntent)
                .build();
        return mNotificacion;
    }

    public Notification getNotificacionNeutra(String paquete, Long restante, Long proporcion) {
        createNotificationChannel();
        if (mToqueDeQuedaHandler.isToqueDeQueda()){
            mNotificacion = getNotificationToqueDeQueda(paquete);
        }else {
            mNotificacion = new Notification.Builder(mContext, CHANNEL_ID)
                    .setContentTitle(packageToLabel(paquete))
                    .setContentText(mContext.getText(R.string.content_notificacion_neutra) + milisToTime(restante / proporcion))
                    .setSmallIcon(R.drawable.notificacion_neutral)
                    .setContentIntent(mPendingIntent)
                    .build();
        }
        return mNotificacion;
    }

    public Notification getNotificacionNegativa(String paquete, Long restante, Long proporcion) {
        createNotificationChannel();
        if (mToqueDeQuedaHandler.isToqueDeQueda()){
            mNotificacion = getNotificationToqueDeQueda(paquete);
        }else {
            mNotificacion = new Notification.Builder(mContext, CHANNEL_ID)
                    .setContentTitle(packageToLabel(paquete))
                    .setContentText(mContext.getText(R.string.content_notificacion_negativa) + milisToTime(restante / proporcion) + mContext.getText(R.string.y_restando_tiempo))
                    .setSmallIcon(R.drawable.notificacion_frio)
                    .setContentIntent(mPendingIntent)
                    .build();
        }
        return mNotificacion;
    }

    public Notification getNotificacionPositiva(TimeLogHandler logger, String paquete, Long restante, Long proporcion) {
        createNotificationChannel();
        if (mToqueDeQuedaHandler.isToqueDeQueda()){
            mNotificacion = getNotificationToqueDeQueda(paquete);
        }else {
            if (logger.ifAllAppConditionsMet(paquete)) {
                mNotificacion = new Notification.Builder(mContext, CHANNEL_ID)
                        .setContentTitle(packageToLabel(paquete))
                        .setContentText(mContext.getText(R.string.content_notificacion_positiva) + milisToTime(restante / proporcion) + mContext.getText(R.string.y_sumando_tiempo))
                        .setSmallIcon(R.drawable.notificacion_caliente)
                        .setContentIntent(mPendingIntent)
                        .build();
            } else {
                mNotificacion = new Notification.Builder(mContext, CHANNEL_ID)
                        .setContentTitle(packageToLabel(paquete))
                        .setContentText(mContext.getText(R.string.content_notificacion_positiva) + " " + mContext.getText(R.string.condiciones_no_cumplidas))
                        .setSmallIcon(R.drawable.notificacion_neutral)
                        .setContentIntent(mPendingIntent)
                        .build();
            }
        }
        return mNotificacion;
    }

    public Notification getCustomNotification(String paquete, String texto, String ticker) {
        createNotificationChannel();
        mNotificacion = new Notification.Builder(mContext, CHANNEL_ID)
                .setContentTitle(packageToLabel(paquete))
                .setContentText(texto)
                .setSmallIcon(R.drawable.notificacion_muslo)
                .setContentIntent(mPendingIntent)
                .setTicker(ticker)
                .build();
        return mNotificacion;
    }

    private Notification getNotificationToqueDeQueda(String paquete){
        mNotificacion = new Notification.Builder(mContext, CHANNEL_ID)
                .setContentTitle(packageToLabel(paquete))
                .setContentText(mContext.getText(R.string.texto_notificacion_toque_de_queda))
                .setSmallIcon(R.drawable.police_badge)
                .setContentIntent(mPendingIntent)
                .build();
        return mNotificacion;
    }

    private String packageToLabel(String packageName) {
        String label = "";
        try {
            ApplicationInfo pi = mPackageManager.getApplicationInfo(packageName, 0);
            label = String.valueOf(mPackageManager.getApplicationLabel(pi));
        } catch (
                PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return label;
    }

    private String milisToTime(Long milis) {
        Long hours = Math.abs(milis / (1000 * 60 * 60));
        Long minutes = Math.abs(milis % (1000 * 60 * 60)) / (1000 * 60);
        Long seconds = Math.abs(milis % (1000 * 60) / (1000));

        Formatter fm = new Formatter();
        if (milis >= 0) {
            fm.format(" %02d:%02d:%02d ", hours, minutes, seconds);
        } else {
            fm.format(" [ - %02d:%02d:%02d ] ", hours, minutes, seconds);
        }

        return fm.toString();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && serviceChannel == null) {
            serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "ColdTurkeyYourself Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setSound(null, null);
            NotificationManager manager = mContext.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void registerOnOffBroadcast(Context context) {
        br = new WatchdogScreenOnOffReceiver();
        br.addFeedbackListener((FeedbackListener<Object>) (tipo, feedback, args) -> {
            int ltipo = -1;
            if (tipo == WatchdogScreenOnOffReceiver.APAGADA) {
                ltipo = FEEDBACK_APAGADA;
            } else if (tipo == WatchdogScreenOnOffReceiver.ENCENDIDA) {
                ltipo = FEEDBACK_ENCENDIDA;
            }
            giveFeedback(ltipo, null);
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(br, filter);
    }

    public void unregisterOnOffBroadcast(Context context) {
        if (br != null) {
            context.unregisterReceiver(br);
        }
    }

    @Override
    public void giveFeedback(int tipo, Object feedback) {
        feedbackList.forEach((listener) -> {
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Object> listener) {
        feedbackList.add(listener);
    }

    public boolean ifPhoneIsUnlocked() {
        boolean isPhoneLock = false;
        if (mContext != null) {
            KeyguardManager myKM = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            if (myKM != null && myKM.isKeyguardLocked()) {
                isPhoneLock = true;
            }
        }
        Log.d(TAG, "phone is locked: " + isPhoneLock);
        return !isPhoneLock;
    }

    public boolean ifPhoneIsOn() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        Log.d(TAG, "phone is on: " + isScreenOn);
        return isScreenOn;
    }

    public boolean ifPhoneOnAndUnlocked() {
        return ifPhoneIsUnlocked() & ifPhoneIsOn();
    }
}
