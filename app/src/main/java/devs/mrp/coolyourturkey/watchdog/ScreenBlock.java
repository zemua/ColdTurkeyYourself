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
    private Application mApp;

    public ScreenBlock(Context context, Application app) {
        mContext = context;
        mApp = app;
    }

    public void go() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        new GenericTimedToaster(mApp).noticeMessage(mContext.getString(R.string.app_bloqued));
        mContext.startActivity(intent);
    }
}
