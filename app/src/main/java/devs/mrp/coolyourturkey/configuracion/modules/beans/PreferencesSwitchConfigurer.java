package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.view.View;
import android.widget.Switch;

import java.util.List;
import java.util.function.Supplier;

import devs.mrp.coolyourturkey.comun.ClickListenerConfigurer;
import devs.mrp.coolyourturkey.comun.UiViewConfigurer;
import devs.mrp.coolyourturkey.comun.ViewDisabler;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class PreferencesSwitchConfigurer extends UiViewConfigurer<Switch, PreferencesBooleanEnum> {

    private static final String TAG = PreferencesSwitchConfigurer.class.getSimpleName();

    private MisPreferencias misPreferencias;
    private ClickListenerConfigurer<Switch, PreferencesBooleanEnum> clickListenerFactory;

    public PreferencesSwitchConfigurer(MisPreferencias prefs,
                                       ClickListenerConfigurer<Switch, PreferencesBooleanEnum> listenerFactory,
                                       View parent,
                                       Integer resourceId,
                                       PreferencesBooleanEnum identifier,
                                       List<Supplier<Boolean>> requiredFalseEnablers,
                                       List<Supplier<Boolean>> requiredTrueEnablers,
                                       ViewDisabler viewDisabler) {
        super(parent, resourceId, identifier, requiredFalseEnablers, requiredTrueEnablers, viewDisabler);
        this.misPreferencias = prefs;
        this.clickListenerFactory = listenerFactory;
    }

    @Override
    protected Switch fromView(View view) throws InvalidViewTypeException {
        if (view instanceof Switch) {
            return (Switch) view;
        }
        throw new InvalidViewTypeException("View is not of type Switch");
    }

    @Override
    protected void attachListeners(Switch aSwitch, PreferencesBooleanEnum identifier) {
        aSwitch.setOnClickListener(clickListenerFactory.getListener(identifier));
    }

    @Override
    protected void setDefaultState(Switch aSwitch, PreferencesBooleanEnum identifier) {
        aSwitch.setChecked(misPreferencias.getBoolean(identifier, identifier.getDefaultState()));
    }

}
