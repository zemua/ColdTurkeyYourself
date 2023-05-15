package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.view.View;
import android.widget.Switch;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class LockdownNeutralClickListenerFactory extends ClickListenerWithConfirmationFactoryTemplate<Switch> {

    public LockdownNeutralClickListenerFactory(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        super(preferencias, dialogWithDelayPresenter);
    }

    @Override
    protected Switch fromView(View view) throws InvalidViewTypeException {
        return null;
    }

    @Override
    protected boolean shouldShowConfirmationDialog(Switch aSwitch) {
        return false;
    }

    @Override
    protected void doOnDialogAcceptAction(Switch aSwitch) {

    }

    @Override
    protected void doOnDialogRejectAction(Switch aSwitch) {

    }

    @Override
    protected String getEventId() {
        return null;
    }
}
