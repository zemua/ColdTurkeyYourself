package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.view.View;
import android.widget.Switch;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.UiViewBuilder;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class LockdownNeutralDecreaseViewBuilder extends UiViewBuilder<Switch>  {

    private static final String TAG = LockdownNeutralDecreaseViewBuilder.class.getSimpleName();

    private MisPreferencias misPreferencias;
    private ClickListenerWithConfirmationFactoryTemplate<Switch> clickListenerFactory;

    public LockdownNeutralDecreaseViewBuilder(MisPreferencias prefs, ClickListenerWithConfirmationFactoryTemplate<Switch> listenerFactory) {
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
    protected void attachListeners(Switch aSwitch) {
        aSwitch.setOnClickListener(clickListenerFactory.getListener());
    }

    @Override
    protected void setInitialState(Switch aSwitch) {
        aSwitch.setChecked(misPreferencias.getBoolean(PreferencesEnum.LOCKDOWN_NEUTRAL_DECREASE, true));
    }

}
