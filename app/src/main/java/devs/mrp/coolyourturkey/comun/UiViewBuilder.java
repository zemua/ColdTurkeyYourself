package devs.mrp.coolyourturkey.comun;

import android.util.Log;
import android.view.View;

import java.util.Optional;

import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public abstract class UiViewBuilder<T extends View> {

    private static final String TAG = UiViewBuilder.class.getSimpleName();

    public Optional<T> buildElement(View parent, int resourceId) {
        View v = parent.findViewById(resourceId);
        try {
            return Optional.ofNullable(fromView(v)).map(this::initializeObject);
        } catch (InvalidViewTypeException e) {
            Log.e(TAG, e.toString());
        }
        return Optional.empty();
    }

    protected abstract T fromView(View view) throws InvalidViewTypeException;

    private T initializeObject(T aSwitch) {
        setInitialState(aSwitch);
        attachListeners(aSwitch);
        return aSwitch;
    }

    protected abstract void attachListeners(T aSwitch);

    protected abstract void setInitialState(T aSwitch);

}
