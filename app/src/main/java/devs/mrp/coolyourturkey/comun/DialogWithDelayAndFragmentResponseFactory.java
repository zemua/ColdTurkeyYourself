package devs.mrp.coolyourturkey.comun;

import androidx.fragment.app.FragmentManager;

public interface DialogWithDelayAndFragmentResponseFactory {
    public DialogWithDelayShower getDialog(int iconResId, String title, String message, String requestKey);
}
