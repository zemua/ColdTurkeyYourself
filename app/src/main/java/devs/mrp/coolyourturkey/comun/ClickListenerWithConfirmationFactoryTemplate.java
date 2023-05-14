package devs.mrp.coolyourturkey.comun;

import android.util.Log;
import android.view.View;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public abstract class ClickListenerWithConfirmationFactoryTemplate<T> {

    private static final String TAG = ClickListenerWithConfirmationFactoryTemplate.class.getSimpleName();

    protected MisPreferencias preferencias;
    protected DialogWithDelayPresenter dialogWithDelayPresenter;

    public ClickListenerWithConfirmationFactoryTemplate(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        this.preferencias = preferencias;
        this.dialogWithDelayPresenter = dialogWithDelayPresenter;
    }

    public final View.OnClickListener getListener() {
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

    protected abstract T fromView(View view) throws InvalidViewTypeException;

    private void performAction(T s) {
        if (isPositiveAction(s)) {
            if (isDialogOnPositive()) {
                performPositiveButtonAction(s);
            } else {
                doPositiveAction(s);
            }
        } else {
            if (isDialogOnNegative()) {
                performNegativeButtonAction(s);
            } else {
                doNegativeAction(s);
            }
        }
    }

    protected abstract boolean isPositiveAction(T t);

    protected abstract boolean isDialogOnPositive();

    private void performPositiveButtonAction(T s) {
        String positiveSuffix = "_positive";
        dialogWithDelayPresenter.setListener(getEventId() + positiveSuffix, b -> handlePositiveFeedback(b, s));
        dialogWithDelayPresenter.showDialog(getEventId() + positiveSuffix);
    }

    private void handlePositiveFeedback(boolean accept, T s) {
        if (accept) {
            doPositiveAction(s);
        }
    }

    protected abstract void doPositiveAction(T t);

    protected abstract boolean isDialogOnNegative();

    private void performNegativeButtonAction(T s) {
        String negativeSuffix = "_negative";
        dialogWithDelayPresenter.setListener(getEventId() + negativeSuffix, b -> handleNegativeFeedback(b, s));
        dialogWithDelayPresenter.showDialog(getEventId() + negativeSuffix);
    }

    private void handleNegativeFeedback(boolean accept, T s) {
        if (accept) {
            doNegativeAction(s);
        }
    }

    protected abstract void doNegativeAction(T t);

    protected abstract String getEventId();

}
