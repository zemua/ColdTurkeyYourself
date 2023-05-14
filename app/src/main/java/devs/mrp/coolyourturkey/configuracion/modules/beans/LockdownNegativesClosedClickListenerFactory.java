package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.view.View;
import android.widget.Switch;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class LockdownNegativesClosedClickListenerFactory extends ClickListenerWithConfirmationFactoryTemplate<Switch> {

    public LockdownNegativesClosedClickListenerFactory(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        super(preferencias, dialogWithDelayPresenter);
    }

    @Override
    protected Switch fromView(View view) throws InvalidViewTypeException {
        if (view instanceof Switch) {
            return (Switch) view;
        }
        throw new InvalidViewTypeException("View is not of type Switch: " + view.toString());
    }

    @Override
    protected boolean isPositiveAction(Switch aSwitch) {
        return aSwitch.isChecked();
    }

    @Override
    protected boolean isDialogOnPositive() {
        return false;
    }

    @Override
    protected void doPositiveAction(Switch aSwitch) {
        preferencias.setBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK, true);
    }

    @Override
    protected boolean isDialogOnNegative() {
        return true;
    }

    @Override
    protected void doNegativeAction(Switch aSwitch) {
        preferencias.setBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK, false);
        aSwitch.setChecked(false);
    }

    @Override
    protected String getEventId() {
        return PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK.getValue();
    }
}
