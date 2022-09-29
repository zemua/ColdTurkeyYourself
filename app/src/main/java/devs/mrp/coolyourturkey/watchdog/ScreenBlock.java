package devs.mrp.coolyourturkey.watchdog;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import devs.mrp.coolyourturkey.MainActivity;
import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.GenericTimedToaster;

import static android.content.Context.WINDOW_SERVICE;

public class ScreenBlock {

    private Context mContext;
    private static View mV;
    private static WindowManager mWindowManager;
    private Handler mHandler;
    private Application mApp;

    public ScreenBlock(Context context, Application app) {
        mContext = context;
        mHandler = getMainHandler();
        mApp = app;
    }

    public void go() {
        Intent intent = new Intent(mContext, MainActivity.class);
        /*intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        new GenericTimedToaster(mApp).noticeMessage(mContext.getString(R.string.app_bloqued));
        mContext.startActivity(intent);
        /*if (!estamosBloqueando()) {
            LayoutInflater inflater = mContext.getSystemService(LayoutInflater.class);
            mV = inflater.inflate(R.layout.screen_block, null, false);
            mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);

            Button okButton = (Button) mV.findViewById(R.id.boton_desbloquea);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pulsarHomeButton();
                    desbloquear();
                }
            });

            WindowManager.LayoutParams params;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            } else {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }
            Runnable r = getBlockingRunnable(mWindowManager, mV, params);
            mHandler.post(r);
        }*/
    }

    private Runnable getBlockingRunnable(WindowManager wm, View v, WindowManager.LayoutParams params){
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                wm.addView(v, params);
            }
        };
        return myRunnable;
    }

    private Runnable getDesbloqueadorRunnable(WindowManager wm, View v){
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                wm.removeViewImmediate(v);
            }
        };
        return myRunnable;
    }

    private Handler getMainHandler(){
        return new Handler(mContext.getApplicationContext().getMainLooper());
    }

    public boolean estamosBloqueando(){
        if (mV != null && mV.getWindowToken() != null) {
            return true;
        }
        return false;
    }

    public void pulsarHomeButton(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(startMain);
    }

    public void desbloquear(){
        if (mWindowManager != null && mV != null){
            if (estamosBloqueando()) {
                Runnable r = getDesbloqueadorRunnable(mWindowManager, mV);
                mHandler.post(r);
            }
        }
    }
}
