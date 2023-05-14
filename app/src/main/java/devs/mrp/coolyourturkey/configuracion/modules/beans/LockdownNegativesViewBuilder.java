package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.util.Log;
import android.view.View;
import android.widget.Switch;

import java.util.Optional;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.UiViewBuilder;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class LockdownNegativesViewBuilder implements UiViewBuilder<Switch> {

    private static final String TAG = LockdownNegativesViewBuilder.class.getSimpleName();

    private MisPreferencias misPreferencias;
    private ClickListenerWithConfirmationFactoryTemplate<Switch> clickListenerFactory;

    public LockdownNegativesViewBuilder(MisPreferencias prefs, ClickListenerWithConfirmationFactoryTemplate<Switch> listenerFactory) {
        this.misPreferencias = prefs;
        this.clickListenerFactory = listenerFactory;
    }

    public Optional<Switch> buildElement(View parent, int resourceId) {
        View v = parent.findViewById(resourceId);
        try {
            return Optional.ofNullable(fromView(v)).map(this::initializeObject);
        } catch (InvalidViewTypeException e) {
            Log.e(TAG, e.toString());
        }
        return Optional.empty();
    }

    protected Switch fromView(View view) throws InvalidViewTypeException {
        if (view instanceof Switch) {
            return (Switch) view;
        }
        throw new InvalidViewTypeException("View is not of type Switch");
    }

    private Switch initializeObject(Switch aSwitch) {
        attachListeners(aSwitch);
        setInitialState(aSwitch);
        return aSwitch;
    }

    protected void attachListeners(Switch aSwitch) {
        aSwitch.setOnClickListener(clickListenerFactory.getListener());
    }

    protected void setInitialState(Switch aSwitch) {
        Boolean isChecked = misPreferencias.getBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK, true);
        aSwitch.setChecked(isChecked);
    }
}
