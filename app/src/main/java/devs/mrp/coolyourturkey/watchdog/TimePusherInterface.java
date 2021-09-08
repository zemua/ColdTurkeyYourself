package devs.mrp.coolyourturkey.watchdog;

import androidx.lifecycle.LifecycleOwner;

public interface TimePusherInterface {

    public void push(long epoch, long acumulado);

    public void add(long epoch, long acumulado, LifecycleOwner owner);

}
