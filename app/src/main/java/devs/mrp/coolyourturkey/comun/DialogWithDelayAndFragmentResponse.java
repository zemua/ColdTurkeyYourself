package devs.mrp.coolyourturkey.comun;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

public class DialogWithDelayAndFragmentResponse extends DialogWithDelay implements DialogWithDelayShower{

    public static final String RESULT_KEY = "result_key";

    private FragmentManager fragmentManager;
    private String requestKey;

    public DialogWithDelayAndFragmentResponse(int iconResId, String title, String message, FragmentManager fragmentManager, String requestKey) {
        super(iconResId, title, message);
        this.fragmentManager = fragmentManager;
        this.requestKey = requestKey;
    }

    @Override
    protected void sendResult(int resultCode, boolean aceptado) {
        if (aceptado) {
            giveFeedback(FEEDBACK_ALERT_DIALOG_ACEPTAR, mDialogo);
        } else {
            giveFeedback(FEEDBACK_ALERT_DIALOG_RECHAZAR, mDialogo);
        }
        Bundle result = new Bundle();
        result.putBoolean(RESULT_KEY, aceptado);
        fragmentManager.setFragmentResult(requestKey, result);
    }
}
