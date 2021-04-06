package devs.mrp.coolyourturkey.watchdog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WatchdogStarter watchdogStarter = new WatchdogStarter(context);
        watchdogStarter.startService();
    }
}
