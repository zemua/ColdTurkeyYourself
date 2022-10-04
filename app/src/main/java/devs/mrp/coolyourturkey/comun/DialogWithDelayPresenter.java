package devs.mrp.coolyourturkey.comun;

import java.util.function.Consumer;

public interface DialogWithDelayPresenter {
    public void setListener(String requestKey, Consumer<Boolean> consumer);
    public void showDialog(String requestKey, String titulo, String mensaje, int iconoResId);
}
