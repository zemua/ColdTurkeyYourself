package devs.mrp.coolyourturkey.comun;

import android.util.Log;
import android.view.View;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.exceptions.InvalidViewTypeException;

public abstract class ClickListenerWithConfirmationFactoryTemplate<T, I> {

    private static final String TAG = ClickListenerWithConfirmationFactoryTemplate.class.getSimpleName();

    protected MisPreferencias preferencias;
    protected DialogWithDelayPresenter dialogWithDelayPresenter;

    public ClickListenerWithConfirmationFactoryTemplate(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        this.preferencias = preferencias;
        this.dialogWithDelayPresenter = dialogWithDelayPresenter;
    }

    public final View.OnClickListener getListener(I identifier) {
        return view -> {
            handleClick(view, identifier);
        };
    }

    private void handleClick(View view, I identifier) {
        try {
            performAction(fromView(view), identifier);
        } catch (InvalidViewTypeException e) {
            Log.e(TAG, e.toString());
        }
    }

    protected abstract T fromView(View view) throws InvalidViewTypeException;

    private void performAction(T s, I identifier) {
        if (isNegativeAction(s)) {
            performButtonAction(s, identifier);
        } else {
            doOnPositiveAction(s, identifier);
        }
    }

    protected abstract boolean isNegativeAction(T t);

    private void performButtonAction(T s, I identifier) {
        dialogWithDelayPresenter.setListener(getEventId(identifier), b -> handleFeedback(b, s, identifier));
        dialogWithDelayPresenter.showDialog(getEventId(identifier));
    }

    private void handleFeedback(boolean accept, T s, I identifier) {
        if (accept) {
            doOnNegativeDialogAcceptAction(s, identifier);
        } else {
            doOnNegativeDialogRejectAction(s, identifier);
        }
    }

    protected abstract void doOnNegativeDialogAcceptAction(T t, I identifier);

    protected abstract void doOnNegativeDialogRejectAction(T t, I identifier);

    protected abstract void doOnPositiveAction(T t, I identifier);

    protected abstract String getEventId(I identifier);

}
