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
        if (isNegativeAction(s)) {
            performButtonAction(s);
        } else {
            doOnPositiveAction(s);
        }
    }

    protected abstract boolean isNegativeAction(T t);

    private void performButtonAction(T s) {
        dialogWithDelayPresenter.setListener(getEventId(), b -> handleFeedback(b, s));
        dialogWithDelayPresenter.showDialog(getEventId());
    }

    private void handleFeedback(boolean accept, T s) {
        if (accept) {
            doOnNegativeDialogAcceptAction(s);
        } else {
            doOnNegativeDialogRejectAction(s);
        }
    }

    protected abstract void doOnNegativeDialogAcceptAction(T t);

    protected abstract void doOnNegativeDialogRejectAction(T t);

    protected abstract void doOnPositiveAction(T t);

    protected abstract String getEventId();

}
