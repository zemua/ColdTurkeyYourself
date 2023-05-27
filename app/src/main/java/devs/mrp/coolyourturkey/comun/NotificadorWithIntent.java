package devs.mrp.coolyourturkey.comun;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

public class NotificadorWithIntent {

    public static Notification notifyWithIntentAndCustomSound(Integer iconResourceId, String title, String content, Context context, Intent intent, String channelId, int requestCode, Uri soundUri){
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(iconResourceId)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(soundUri)
                .setAutoCancel(true);
        Notification n = builder.build();
        return n;
    }

    public static Notification notifyWithIntent(Integer iconResourceId, String title, String content, Context context, Intent intent, String channelId, int requestCode){
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(iconResourceId)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true);
        Notification n = builder.build();
        return n;
    }

    public static void notify (Notification n, Context context, Integer notificationId){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationId, n);
    }

}
