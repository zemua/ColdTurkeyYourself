package devs.mrp.coolyourturkey.watchdog.utils.impl;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.os.HandlerCompat;

import java.util.HashMap;
import java.util.Map;

import devs.mrp.coolyourturkey.watchdog.utils.Notifier;

public class CachedNotifier implements Notifier<String> {

    private static final Map<String,Long> cached = new HashMap<>();
    private static final long period = 1000*60; // 1 minute between same messages
    private Context context;

    public CachedNotifier(Context cntxt) {
        this.context = cntxt;
    }

    @Override
    public void notify(String data) {
        long lastNotified = cached.containsKey(data) ? cached.get(data) : 0L;
        long now = System.currentTimeMillis();
        if (now > lastNotified+period) {
            cached.put(data, now);
            toastInMainThread(context, data, Toast.LENGTH_LONG);
        }
    }

    private void toastInMainThread(Context context, String mensaje, int length){
        HandlerCompat.createAsync(Looper.getMainLooper()).post(() -> {
            Toast toast = Toast.makeText(context, mensaje, length);
            toast.show();
        });
    }
}
