package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.grupos.GroupType;

public interface AdapterHandlerFactory<T> {
    public AdapterHandler<T> getHandler(GroupType groupType, LifecycleOwner owner);
}
