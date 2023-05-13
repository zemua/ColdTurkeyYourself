package devs.mrp.coolyourturkey.configuracion.modules.beans;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Switch;

import devs.mrp.coolyourturkey.comun.ClickListenerFactory;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public class LockdownNegativesClosedClickListenerFactory implements ClickListenerFactory {

    private static final String TAG = LockdownNegativesClosedClickListenerFactory.class.getSimpleName();

    private MisPreferencias preferencias;
    private DialogWithDelayPresenter dialogWithDelayPresenter;

    public LockdownNegativesClosedClickListenerFactory(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        this.preferencias = preferencias;
        this.dialogWithDelayPresenter = dialogWithDelayPresenter;
    }

    @Override
    public OnClickListener getListener() {
        return view -> {
            handleClick(view);
        };
    }

    private void handleClick(View view) {
        try {
            performAction(fromView(view));
        } catch (InvalidViewTypeException e) {
            Log.e(TAG, e.toString());
        }
    }

    private Switch fromView(View view) throws InvalidViewTypeException {
        if (view instanceof Switch) {
            return (Switch) view;
        }
        throw new InvalidViewTypeException("View is not of type Switch: " + view.toString());
    }

    private void performAction(Switch s) {
        if (s.isChecked()) {
            preferencias.setBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK.getValue(), true);
        } else {
            performNegativeButtonAction(s);
        }
    }

    private void performNegativeButtonAction(Switch s) {
        dialogWithDelayPresenter.setListener(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK.getValue(), b -> handleNegativeFeedback(b, s));
        dialogWithDelayPresenter.showDialog(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK.getValue());
    }

    private void handleNegativeFeedback(boolean accept, Switch s) {
        if (accept) {
            preferencias.setBoolean(PreferencesEnum.LOCKDOWN_NEGATIVE_BLOCK.getValue(), false);
            s.setChecked(false);
        }
    }
}
