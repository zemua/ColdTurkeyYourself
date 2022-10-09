package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.checkperformer.CheckPerformerActivity;
import devs.mrp.coolyourturkey.comun.NotificadorWithIntent;

public class RandomCheckWorker extends Worker {

    private final String TAG = "RandomCheckWorker";

    public static final String NOTIFICATION_CHANNEL_ID = "notification.channel.for.random.checks";
    public static final Integer NOTIFICATION_ID = 55;
    public static final String KEY_FOR_BLOCK_IN_BUNDLE = "key.for.block.in.bundle";
    public static final String KEY_FOR_POSITIVE_QUESTION = "key.for.positive.question";
    public static final String KEY_FOR_NEGATIVE_QUESTION = "key.for.negative.question";
    public static final String KEY_FOR_BLOCK_ID = "key.for.block.id";
    public static final String KEY_FOR_TIMESTAMP = "key.for.timestamp";
    public static final String KEY_FOR_PREMIO = "key.for.premio";

    private Context mContext;
    //private MisPreferencias misPreferencias;

    public RandomCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        //misPreferencias = new MisPreferencias(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "do work");
        Intent intent = new Intent(mContext, CheckPerformerActivity.class);
        Integer blockId = getInputData().getInt(CheckManager.EXTRA_BLOCK_ID, -1);
        String blockName = getInputData().getString(CheckManager.EXTRA_BLOCK_NAME);
        intent.putExtra(KEY_FOR_BLOCK_ID, blockId);
        intent.putExtra(KEY_FOR_TIMESTAMP, System.currentTimeMillis());
        Notification n;
        n = NotificadorWithIntent.notifyWithIntent(R.drawable.seal, mContext.getString(R.string.notification_channel_for_random_checks_name) + " - " + blockName, mContext.getString(R.string.notification_channel_for_random_checks_description), mContext, intent, NOTIFICATION_CHANNEL_ID, blockId);
        NotificadorWithIntent.notify(n, mContext, (NOTIFICATION_ID+blockId));

        return Result.success();
    }
}
