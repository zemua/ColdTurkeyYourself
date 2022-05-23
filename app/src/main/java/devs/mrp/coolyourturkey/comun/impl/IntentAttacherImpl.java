package devs.mrp.coolyourturkey.comun.impl;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import devs.mrp.coolyourturkey.comun.IntentAttacher;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;

public class IntentAttacherImpl implements IntentAttacher {

    private static final String bundleKey = "devs.mrp.coolyourturkey.comun.impl.IntentAttacherImpl.bundle.key";

    private Bundle bundle = new Bundle();
    private boolean isRead = false;

    public IntentAttacherImpl(Intent intent) {
        setBundle(intent);
    }

    @Override
    public void attach(String name, Object object) {
        bundle.putBinder(name, new ObjectWrapperForBinder(object));
    }

    @Override
    public Object read(String name, Class<?> type, Object defaultValue) {
        IBinder binder = bundle.getBinder(name);
        if (!(binder instanceof ObjectWrapperForBinder)) {
            return defaultValue;
        }
        ObjectWrapperForBinder wrapper = (ObjectWrapperForBinder) binder;
        Object object = wrapper.getData();
        if (!(type.isInstance(object))) {
            return defaultValue;
        }
        return object;
    }

    @Override
    public boolean isRead() {
        return isRead;
    }

    @Override
    public boolean contains(String name) {
        return bundle.containsKey(name);
    }

    private void setBundle(Intent intent) {
        if (!intent.hasExtra(bundleKey)) {
            intent.putExtra(bundleKey, bundle);
        } else {
            bundle = intent.getBundleExtra(bundleKey);
            isRead = true;
        }
    }
}
