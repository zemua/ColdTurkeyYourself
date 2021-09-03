package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.app.Notification;
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
import devs.mrp.coolyourturkey.comun.NotificadorWithIntent;
import devs.mrp.coolyourturkey.comun.TransferWithBinders;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public class RandomCheckWorker extends Worker {

    private final String TAG = "RandomCheckWorker";

    public static final String NOTIFICATION_CHANNEL_ID = "notification.channel.for.random.checks";
    public static final String KEY_FOR_BLOCK_IN_BUNDLE = "key.for.block.in.bundle";
    public static final String KEY_FOR_POSITIVE_QUESTION = "key.for.positive.question";
    public static final String KEY_FOR_NEGATIVE_QUESTION = "key.for.negative.question";
    public static final String KEY_FOR_BLOCK_ID = "key.for.block.id";
    public static final String KEY_FOR_PREMIO = "key.for.premio";
    private static final int notificationId = 84;

    private static ArrayDeque<AbstractTimeBlock> mBlock = new ArrayDeque<>();
    //private static Notificador mNotificador;
    private static Context mContext;

    public RandomCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        AbstractTimeBlock block = mBlock.pollLast();
        Intent intent = new Intent(mContext, CheckPerformerActivity.class);
        if (block.getNegativeChecks() != null && block.getNegativeChecks().size() > 0) {
            intent.putExtra(KEY_FOR_NEGATIVE_QUESTION, block.getNegativeChecks().get((int) Math.random() * block.getNegativeChecks().size()).getQuestion());
        }
        if (block.getPositiveChecks() != null && block.getPositiveChecks().size() > 0) {
            PositiveCheck pCheck = block.getPositiveChecks().get((int) Math.random() * block.getPositiveChecks().size());
            intent.putExtra(KEY_FOR_POSITIVE_QUESTION, pCheck.getQuestion());
            intent.putExtra(KEY_FOR_PREMIO, (block.getMinimumLapse() + ((block.getMaximumLapse()-block.getMinimumLapse())/2)) * pCheck.getMultiplicador());
        }
        intent.putExtra(KEY_FOR_BLOCK_ID, block.getId());
        //TransferWithBinders.addToSend(intent, KEY_FOR_BLOCK_IN_BUNDLE, mBlock.pollLast());
        //PendingIntent pending = PendingIntent.getActivity(mContext, 0, intent, 0);
        //mNotificador.createNotification(R.drawable.seal, mContext.getString(R.string.notification_channel_for_random_checks_name), mContext.getString(R.string.notification_channel_for_random_checks_description), NOTIFICATION_CHANNEL_ID, notificationId, pending);
        Notification n = NotificadorWithIntent.notifyWithIntent(R.drawable.seal, mContext.getString(R.string.notification_channel_for_random_checks_name), mContext.getString(R.string.notification_channel_for_random_checks_description), mContext, intent, NOTIFICATION_CHANNEL_ID);
        NotificadorWithIntent.notify(n, mContext);

        return Result.success();
    }

    public static void configureNotification(Notificador notificador, Context context) {
        //mNotificador = notificador;
        //mContext = context;
    }

    public static void addBlock(AbstractTimeBlock block) {
        mBlock.addFirst(block);
    }
}
