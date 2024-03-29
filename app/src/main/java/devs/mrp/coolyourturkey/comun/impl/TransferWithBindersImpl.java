package devs.mrp.coolyourturkey.comun.impl;

import android.content.Intent;
import android.os.Bundle;

import java.util.Optional;

import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;

public class TransferWithBindersImpl {

    public static Bundle addToSend(Intent intent, String keyToReadLater, Object object) {
        Bundle bundle = new Bundle();
        bundle.putBinder(keyToReadLater, new ObjectWrapperForBinder(object));
        intent.putExtras(bundle);
        return bundle;
    }

    public static Optional<Object> receiveAndRead(Intent intent, String key) {
        Optional<Object> optional = Optional.empty();
        if (intent.hasExtra(key)) {
            optional = Optional.of(((ObjectWrapperForBinder) intent.getExtras().getBinder(key)).getData());
        }
        return optional;
    }

}
