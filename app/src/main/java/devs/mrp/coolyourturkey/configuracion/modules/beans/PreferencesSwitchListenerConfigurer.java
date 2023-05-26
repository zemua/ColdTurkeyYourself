package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.view.View;
import android.widget.Switch;

import java.util.function.Function;

import devs.mrp.coolyourturkey.comun.ClickListenerConfigurer;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class PreferencesSwitchListenerConfigurer extends ClickListenerConfigurer<Switch, PreferencesBooleanEnum> {

    private Runnable doOnChangeAction;
    private Function<Switch,Boolean> conditionForNegative;

    public PreferencesSwitchListenerConfigurer(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        super(preferencias, dialogWithDelayPresenter);
        this.doOnChangeAction = () -> {};
        this.conditionForNegative = v -> false;
    }

    public PreferencesSwitchListenerConfigurer(MisPreferencias preferencias,
                                               DialogWithDelayPresenter dialogWithDelayPresenter,
                                               Runnable doOnChangeAction,
                                               Function<Switch,Boolean> conditionForNegative) {
        super(preferencias, dialogWithDelayPresenter);
        this.doOnChangeAction = doOnChangeAction != null ? doOnChangeAction : () -> {};
        this.conditionForNegative = conditionForNegative;
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
        return conditionForNegative.apply(aSwitch);
    }

    @Override
    protected void doOnNegativeDialogAcceptAction(Switch aSwitch, PreferencesBooleanEnum identifier) {
        boolean negativePosition = negativePosition(aSwitch);
        preferencias.setBoolean(identifier, negativePosition);
        aSwitch.setChecked(negativePosition);
        doOnChangeAction.run();
    }

    @Override
    protected void doOnNegativeDialogRejectAction(Switch aSwitch, PreferencesBooleanEnum identifier) {
        aSwitch.setChecked(positivePosition(aSwitch));
    }

    @Override
    protected void doOnPositiveAction(Switch aSwitch, PreferencesBooleanEnum identifier) {
        boolean positivePosition = positivePosition(aSwitch);
        preferencias.setBoolean(identifier, positivePosition);
        doOnChangeAction.run();
    }

    private boolean negativePosition(Switch s) {
        if (isNegativeAction(s)) {
            return s.isChecked();
        } else {
            return !s.isChecked();
        }
    }

    private boolean positivePosition(Switch s) {
        if (!isNegativeAction(s)) {
            return s.isChecked();
        } else {
            return !s.isChecked();
        }
    }

    @Override
    protected String getEventId(PreferencesBooleanEnum identifier) {
        return identifier.getValue();
    }
}
