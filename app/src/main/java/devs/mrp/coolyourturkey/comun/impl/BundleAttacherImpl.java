package devs.mrp.coolyourturkey.comun.impl;

import android.os.Bundle;
import android.os.IBinder;

import devs.mrp.coolyourturkey.comun.BundleAttacher;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;

public class BundleAttacherImpl implements BundleAttacher {

    private Bundle mBundle;

    public BundleAttacherImpl(Bundle bundle) {
        this.mBundle = bundle;
    }

    @Override
    public void attach(String name, Object object) {
        mBundle.putBinder(name, new ObjectWrapperForBinder(object));
    }

    @Override
    public Object read(String name, Class<?> type, Object defaultValue) {
        if (!mBundle.containsKey(name)){
            return defaultValue;
        }
        IBinder binder = mBundle.getBinder(name);
        if (!(binder instanceof ObjectWrapperForBinder)) {
            return defaultValue;
        }
        ObjectWrapperForBinder wrapper = (ObjectWrapperForBinder) binder;
        Object object = wrapper.getData();
        return type.isInstance(object) ? object : defaultValue;
    }

    @Override
    public boolean contains(String name) {
        return mBundle.containsKey(name);
    }
}
