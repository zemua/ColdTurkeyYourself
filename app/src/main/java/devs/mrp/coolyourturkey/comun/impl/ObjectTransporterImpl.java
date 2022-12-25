package devs.mrp.coolyourturkey.comun.impl;

import android.content.Intent;
import android.os.Bundle;

import java.util.Optional;

import devs.mrp.coolyourturkey.comun.ObjectTransporter;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;

public class ObjectTransporterImpl implements ObjectTransporter {
    public void addToIntent(Intent intent, String keyToReadLater, Object object) {
        Bundle bundle = new Bundle();
        addToBundle(bundle, keyToReadLater, object);
        intent.putExtras(bundle);
    }

    public Optional<Object> getFromIntent(Intent intent, String key) {
        Optional<Object> optional = Optional.empty();
        if (intent.hasExtra(key)) {
            optional = Optional.of(intent.getExtras())
                    .flatMap(extras -> Optional.ofNullable((ObjectWrapperForBinder)extras.getBinder(key)))
                    .map(ObjectWrapperForBinder::getData);
        }
        return optional;
    }

    public void addToBundle(Bundle bundle, String keyToReadLater, Object object) {
        bundle.putBinder(keyToReadLater, new ObjectWrapperForBinder(object));
    }

    public Optional<Object> getFromBundle(Bundle bundle, String key) {
        return Optional.ofNullable((ObjectWrapperForBinder)bundle.getBinder(key))
                .map(ObjectWrapperForBinder::getData);
    }
}
