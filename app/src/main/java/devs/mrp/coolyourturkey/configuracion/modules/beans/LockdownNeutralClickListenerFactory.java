package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.view.View;
import android.widget.Switch;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class LockdownNeutralClickListenerFactory extends ClickListenerWithConfirmationFactoryTemplate<Switch> {

    public LockdownNeutralClickListenerFactory(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        super(preferencias, dialogWithDelayPresenter);
    }

    @Override
    protected Switch fromView(View view) throws InvalidViewTypeException {
        if (view instanceof Switch) {
            return (Switch) view;
        }
        throw new InvalidViewTypeException("View is not of type Switch");
    }

    @Override
    protected boolean isNegativeAction(Switch aSwitch) {
        return !aSwitch.isChecked();
    }

    @Override
    protected void doOnNegativeDialogAcceptAction(Switch aSwitch) {
        preferencias.setBoolean(PreferencesEnum.LOCKDOWN_NEUTRAL_DECREASE, false);
        aSwitch.setChecked(false);
    }

    @Override
    protected void doOnNegativeDialogRejectAction(Switch aSwitch) {
        aSwitch.setChecked(true);
    }

    @Override
    protected void doOnPositiveAction(Switch aSwitch) {
        preferencias.setBoolean(PreferencesEnum.LOCKDOWN_NEUTRAL_DECREASE, true);
    }

    @Override
    protected String getEventId() {
        return PreferencesEnum.LOCKDOWN_NEUTRAL_DECREASE.getValue();
    }
}
