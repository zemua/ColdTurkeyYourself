package devs.mrp.coolyourturkey.comun;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DialogWithDelayAndFragmentResponse extends DialogWithDelay implements DialogWithDelayShower{

    private static final String BUNDLE_REQUESTKEY = "bundle.request.key";

    public static final String RESULT_KEY = "result_key";

    private String requestKey;

    public DialogWithDelayAndFragmentResponse() {
        super();
    }

    public DialogWithDelayAndFragmentResponse(int iconResId, String title, String message, String requestKey) {
        super(iconResId, title, message);
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
        getActivity().getSupportFragmentManager().setFragmentResult(requestKey, result);
        getParentFragmentManager().setFragmentResult(requestKey, result);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        outstate.putString(BUNDLE_REQUESTKEY, requestKey);
        super.onSaveInstanceState(outstate);
    }

    @Override
    protected void restoreValues(Bundle savedInstanceState) {
        requestKey = savedInstanceState.getString(BUNDLE_REQUESTKEY);
        super.restoreValues(savedInstanceState);
    }
}
