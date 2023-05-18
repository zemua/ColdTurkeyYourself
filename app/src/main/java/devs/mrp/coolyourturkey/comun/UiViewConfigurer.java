package devs.mrp.coolyourturkey.comun;

import android.util.Log;
import android.view.View;

import java.util.Optional;

import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public abstract class UiViewConfigurer<T extends View, I> {

    private static final String TAG = UiViewConfigurer.class.getSimpleName();

    public Optional<T> buildElement(View parent, int resourceId, I identifier) {
        View v = parent.findViewById(resourceId);
        try {
            return Optional.ofNullable(fromView(v)).map(s -> initializeObject(s, identifier));
        } catch (InvalidViewTypeException e) {
            Log.e(TAG, e.toString());
        }
        return Optional.empty();
    }

    protected abstract T fromView(View view) throws InvalidViewTypeException;

    private T initializeObject(T aSwitch, I identifier) {
        setDefaultState(aSwitch, identifier);
        attachListeners(aSwitch, identifier);
        return aSwitch;
    }

    protected abstract void attachListeners(T aSwitch, I identifier);

    protected abstract void setDefaultState(T aSwitch, I identifier);

}