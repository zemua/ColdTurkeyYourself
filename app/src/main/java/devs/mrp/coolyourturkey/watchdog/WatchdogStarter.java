package devs.mrp.coolyourturkey.watchdog;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import androidx.core.content.ContextCompat;

public class WatchdogStarter {

    Context mContext;
    Intent mServiceIntent;
    ServiceConnection mConnection;

    public WatchdogStarter(Context context, ServiceConnection connection){
        mContext = context;
        mConnection = connection;
    }

    public WatchdogStarter(Context context){
        mContext = context;
    }

    public void startService(){
        createServiceIntent();
        ContextCompat.startForegroundService(mContext, mServiceIntent);
        if (mConnection != null) {
            Intent intent = new Intent(mContext, WatchdogService.class);
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

    }

    public void stopService(WatchdogService serv){
        createServiceIntent();
        //mContext.stopService(mServiceIntent);
        serv.pausar();
    }

    private void createServiceIntent(){
        if (mServiceIntent == null){
            mServiceIntent = new Intent(mContext, WatchdogService.class);
        }
    }
}
