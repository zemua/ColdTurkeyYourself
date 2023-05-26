package devs.mrp.coolyourturkey.comun;

import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public abstract class UiViewConfigurer<T extends View, I> {

    private static final String TAG = UiViewConfigurer.class.getSimpleName();

    private View parent;
    private Integer resourceId;
    private I identifier;
    private List<Supplier<Boolean>> requiredFalseEnablers;
    private List<Supplier<Boolean>> requiredTrueEnablers;
    private ViewDisabler viewDisabler;

    public UiViewConfigurer(View parent, int resourceId, I identifier,
                            List<Supplier<Boolean>> requiredFalseEnablers,
                            List<Supplier<Boolean>> requiredTrueEnablers,
                            ViewDisabler viewDisabler) {
        this.parent = parent;
        this.resourceId = resourceId;
        this.identifier = identifier;
        this.requiredTrueEnablers = requiredTrueEnablers;
        this.requiredFalseEnablers = requiredFalseEnablers;
        this.viewDisabler = viewDisabler;
    }

    public Optional<T> buildElement() {
        View v = parent.findViewById(resourceId);
        try {
            Optional<T> viewOptional = Optional.ofNullable(fromView(v));
            viewOptional.ifPresent(aView -> setupDisabler(aView));
            return viewOptional.map(s -> initializeObject(s, identifier));
        } catch (InvalidViewTypeException e) {
            Log.e(TAG, e.toString());
        }
        return Optional.empty();
    }

    private void setupDisabler(T aView) {
        if (viewDisabler == null) {
            return;
        }
        List<Supplier<Boolean>> invertedRequiredFalseEnablers = requiredFalseEnablers.stream().map(supplier -> supplierInverter(supplier)).collect(Collectors.toList());
        List<Supplier<Boolean>> allEnablers = new LinkedList<>();
        allEnablers.addAll(requiredTrueEnablers);
        allEnablers.addAll(invertedRequiredFalseEnablers);
        viewDisabler.addViewConditions(aView, allEnablers);
    }

    private Supplier<Boolean> supplierInverter(Supplier<Boolean> supplier) {
        return () -> !supplier.get();
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
