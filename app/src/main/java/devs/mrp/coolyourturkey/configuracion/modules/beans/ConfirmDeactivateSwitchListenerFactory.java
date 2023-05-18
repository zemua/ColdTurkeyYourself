package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.view.View;
import android.widget.Switch;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class ConfirmDeactivateSwitchListenerFactory extends ClickListenerWithConfirmationFactoryTemplate<Switch, PreferencesBooleanEnum> {

    private List<View> viewsToModify;
    private BiConsumer<Switch,View> modifyAction;

    public ConfirmDeactivateSwitchListenerFactory(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        super(preferencias, dialogWithDelayPresenter);
        viewsToModify = Collections.emptyList();
        this.modifyAction = (s,v) -> {};
    }

    public ConfirmDeactivateSwitchListenerFactory(MisPreferencias preferencias,
                                                  DialogWithDelayPresenter dialogWithDelayPresenter,
                                                  List<View> viewsToModify,
                                                  BiConsumer<Switch,View> modifyAction) {
        super(preferencias, dialogWithDelayPresenter);
        this.viewsToModify = Collections.unmodifiableList(viewsToModify);
        this.modifyAction = modifyAction;
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
        viewsToModify.stream().forEach(v -> modifyAction.accept(aSwitch, v));
    }

    @Override
    protected void doOnNegativeDialogRejectAction(Switch aSwitch, PreferencesBooleanEnum identifier) {
        aSwitch.setChecked(true);
    }

    @Override
    protected void doOnPositiveAction(Switch aSwitch, PreferencesBooleanEnum identifier) {
        preferencias.setBoolean(identifier, true);
        viewsToModify.stream().forEach(v -> v.setEnabled(false));
    }

    @Override
    protected String getEventId(PreferencesBooleanEnum identifier) {
        return identifier.getValue();
    }
}
