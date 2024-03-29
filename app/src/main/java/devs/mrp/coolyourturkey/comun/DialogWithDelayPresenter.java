package devs.mrp.coolyourturkey.comun;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface DialogWithDelayPresenter {
    public void setListener(String requestKey, Consumer<Boolean> consumer);
    public void setListener(String requestKey, BiConsumer<Boolean, Object> consumer);
    public void showDialog(String requestKey, String titulo, String mensaje, int iconoResId);
    public void showDialog(String requestKey, String titulo, String mensaje);
    public void showDialog(String requestKey);
    public void showDialog(String requestKey, Object data);
}
