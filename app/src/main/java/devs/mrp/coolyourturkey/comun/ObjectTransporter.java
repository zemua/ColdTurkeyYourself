package devs.mrp.coolyourturkey.comun;

import android.content.Intent;
import android.os.Bundle;

import java.util.Optional;

public interface ObjectTransporter {
    public void addToIntent(Intent intent, String keyToReadLater, Object object);

    public Optional<Object> getFromIntent(Intent intent, String key);

    public void addToBundle(Bundle bundle, String keyToReadLater, Object object);

    public Optional<Object> getFromBundle(Bundle bundle, String key);
}
