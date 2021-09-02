package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.checkperformer.CheckPerformerActivity;
import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.comun.TransferWithBinders;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public class RandomCheckWorker extends Worker {

    public static final String NOTIFICATION_CHANNEL_ID = "notification.channel.for.random.checks";
    public static final String KEY_FOR_BLOCK_IN_BUNDLE = "key.for.block.in.bundle";
    private static final int notificationId = 84;

    private AbstractTimeBlock mBlock;
    private Notificador mNotificador;
    private Context mContext;

    public RandomCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Intent intent = new Intent(mContext, CheckPerformerActivity.class);
        TransferWithBinders.addToSend(intent, KEY_FOR_BLOCK_IN_BUNDLE, mBlock);
        PendingIntent pending = mNotificador.getPendingIntent(intent);
        mNotificador.createNotification(R.drawable.seal, mContext.getString(R.string.notification_channel_for_random_checks_name), mContext.getString(R.string.notification_channel_for_random_checks_description), NOTIFICATION_CHANNEL_ID, notificationId, pending);

        return Result.success();
    }

    public void configureNotification(AbstractTimeBlock block, Notificador notificador, Context context) {
        this.mNotificador = notificador;
        this.mBlock = block;
        mContext = context;
    }
}
