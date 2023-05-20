package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.view.View;
import android.widget.Switch;

import devs.mrp.coolyourturkey.comun.ClickListenerConfigurer;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class PreferencesSwitchListenerConfigurer extends ClickListenerConfigurer<Switch, PreferencesBooleanEnum> {

    private Runnable doOnChangeAction;

    public PreferencesSwitchListenerConfigurer(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        super(preferencias, dialogWithDelayPresenter);
        doOnChangeAction = () -> {};
    }

    public PreferencesSwitchListenerConfigurer(MisPreferencias preferencias,
                                               DialogWithDelayPresenter dialogWithDelayPresenter,
                                               Runnable doOnChangeAction) {
        super(preferencias, dialogWithDelayPresenter);
        this.doOnChangeAction = doOnChangeAction != null ? doOnChangeAction : () -> {};
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
    protected void doOnNegativeDialogAcceptAction(Switch aSwitch, PreferencesBooleanEnum identifier) {
        preferencias.setBoolean(identifier, false);
        aSwitch.setChecked(false);
        doOnChangeAction.run();
    }

    @Override
    protected void doOnNegativeDialogRejectAction(Switch aSwitch, PreferencesBooleanEnum identifier) {
        aSwitch.setChecked(true);
    }

    @Override
    protected void doOnPositiveAction(Switch aSwitch, PreferencesBooleanEnum identifier) {
        preferencias.setBoolean(identifier, true);
        doOnChangeAction.run();
    }

    @Override
    protected String getEventId(PreferencesBooleanEnum identifier) {
        return identifier.getValue();
    }
}
