package devs.mrp.coolyourturkey.databaseroom.grupo;

import java.util.function.Consumer;

public interface ElementAndGroupFacade {
    public void onPreventClosing(String appName, Consumer<Boolean> onPreventClosingConsumer);
}
