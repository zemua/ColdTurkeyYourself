package devs.mrp.coolyourturkey.listados.callables;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;

import devs.mrp.coolyourturkey.listados.AppLister;

public class ListerConstructor implements Callable<AppLister> {

    private Context mContext;

    public ListerConstructor(Context context) {
        this.mContext = context;
    }

    @Override
    public AppLister call() throws Exception {
        AppLister lister = new AppLister(mContext);
        lister.setNonSystemList();
        return lister;
    }
}
