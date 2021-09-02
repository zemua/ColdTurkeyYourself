package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayDeque;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.checkperformer.CheckPerformerActivity;
import devs.mrp.coolyourturkey.comun.Notificador;
import devs.mrp.coolyourturkey.comun.TransferWithBinders;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public class RandomCheckWorker extends Worker {

    private final String TAG = "RandomCheckWorker";

    public static final String NOTIFICATION_CHANNEL_ID = "notification.channel.for.random.checks";
    public static final String KEY_FOR_BLOCK_IN_BUNDLE = "key.for.block.in.bundle";
    private static final int notificationId = 84;

    private static ArrayDeque<AbstractTimeBlock> mBlock = new ArrayDeque<>();
    private static Notificador mNotificador;
    private static Context mContext;

    public RandomCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "value of context: " + mContext);
        Intent intent = new Intent(mContext, CheckPerformerActivity.class);
        TransferWithBinders.addToSend(intent, KEY_FOR_BLOCK_IN_BUNDLE, mBlock.pollLast());
        PendingIntent pending = mNotificador.getPendingIntent(intent);
        mNotificador.createNotification(R.drawable.seal, mContext.getString(R.string.notification_channel_for_random_checks_name), mContext.getString(R.string.notification_channel_for_random_checks_description), NOTIFICATION_CHANNEL_ID, notificationId, pending);

        return Result.success();
    }

    public static void configureNotification(Notificador notificador, Context context) {
        mNotificador = notificador;
        mContext = context;
    }

    public static void addBlock(AbstractTimeBlock block) {
        mBlock.addFirst(block);
    }
}
