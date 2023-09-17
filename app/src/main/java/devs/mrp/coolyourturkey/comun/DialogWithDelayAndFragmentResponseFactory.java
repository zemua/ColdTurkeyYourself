package devs.mrp.coolyourturkey.comun;

public interface DialogWithDelayAndFragmentResponseFactory {
    public DialogWithDelayShower getDialog(int iconResId, String title, String message, String requestKey, int delaySeconds);
}
