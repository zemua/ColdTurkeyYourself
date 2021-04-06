package devs.mrp.coolyourturkey.watchdog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

import java.util.ArrayList;
import java.util.List;

public class WatchdogScreenOnOffReceiver extends BroadcastReceiver implements Feedbacker<Object> {

    public static final int ENCENDIDA = 0;
    public static final int APAGADA = 1;

    private List<FeedbackListener<Object>> feedbackList = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        int laction = -1;
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            laction = APAGADA;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            laction = ENCENDIDA;
        }
        giveFeedback(laction, null);
    }

    @Override
    public void giveFeedback(int tipo, Object feedback) {
        feedbackList.forEach((listener)->{
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Object> listener) {
        feedbackList.add(listener);
    }
}
