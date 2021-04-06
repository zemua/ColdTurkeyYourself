package devs.mrp.coolyourturkey.workspace;

import android.content.Context;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

public class WspController {

    private static WspController instance;
    private DetectWSpHelper helper;
    private Context mContext;
    private MisPreferencias mMisPreferencias;

    public static WspController getInstance(){
        if (instance == null) {
            instance = new WspController();
            instance.helper = new DetectWSpHelper();
        }
        return instance;
    }

    public static WspController getInstance(Context c, MisPreferencias p){
        WspController wc = getInstance();
        wc.setContext(c);
        wc.setMisPreferencias(p);
        return wc;
    }

    public void setContext(Context c) {
        mContext = c;
    }

    public Context getContext(){
        return mContext;
    }

    public void setMisPreferencias(MisPreferencias p){
        mMisPreferencias = p;
    }

    public MisPreferencias getMisPreferencias(){
        return mMisPreferencias;
    }

    public boolean isDiscountPoints(){
        return helper.needToDiscount(mContext, mMisPreferencias);
    }

}
