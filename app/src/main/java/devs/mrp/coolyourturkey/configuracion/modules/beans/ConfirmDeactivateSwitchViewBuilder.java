package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.view.View;
import android.widget.Switch;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.UiViewBuilder;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class ConfirmDeactivateSwitchViewBuilder extends UiViewBuilder<Switch, PreferencesEnum>  {

    private static final String TAG = ConfirmDeactivateSwitchViewBuilder.class.getSimpleName();

    private MisPreferencias misPreferencias;
    private ClickListenerWithConfirmationFactoryTemplate<Switch, PreferencesEnum> clickListenerFactory;
    private boolean defaultState;

    public ConfirmDeactivateSwitchViewBuilder(MisPreferencias prefs,
                                              ClickListenerWithConfirmationFactoryTemplate<Switch, PreferencesEnum> listenerFactory,
                                              boolean defaultState) {
        this.misPreferencias = prefs;
        this.clickListenerFactory = listenerFactory;
        this.defaultState = defaultState;
    }

    @Override
    protected Switch fromView(View view) throws InvalidViewTypeException {
        if (view instanceof Switch) {
            return (Switch) view;
        }
        throw new InvalidViewTypeException("View is not of type Switch");
    }

    @Override
    protected void attachListeners(Switch aSwitch, PreferencesEnum identifier) {
        aSwitch.setOnClickListener(clickListenerFactory.getListener(identifier));
    }

    @Override
    protected void setInitialState(Switch aSwitch, PreferencesEnum identifier) {
        aSwitch.setChecked(misPreferencias.getBoolean(identifier, defaultState));
    }

}
