package devs.mrp.coolyourturkey.databaseroom.grupo.impl;

import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.databaseroom.grupo.ElementAndGroupFacade;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementType;

public class ElementAndGroupFacadeImpl implements ElementAndGroupFacade {

    private GrupoRepository grupoRepository;
    private ElementToGroupRepository elementToGroupRepository;
    private LifecycleOwner owner;

    public ElementAndGroupFacadeImpl(GrupoRepository grupoRepository, ElementToGroupRepository elementToGroupRepository, LifecycleOwner lifecycleOwner) {
        this.grupoRepository = grupoRepository;
        this.elementToGroupRepository = elementToGroupRepository;
        this.owner = lifecycleOwner;
    }

    @Override
    public void onPreventClosing(String appName, Consumer<Boolean> onPreventClosingConsumer) {
        LiveData<List<ElementToGroup>> elementToGroupLiveData = elementToGroupRepository.findElementOfTypeAndName(ElementType.APP, appName);
        HandlerCompat.createAsync(Looper.getMainLooper()).post(() -> observe(onPreventClosingConsumer, elementToGroupLiveData));
    }

    private void observe(Consumer<Boolean> onPreventClosingConsumer, LiveData<List<ElementToGroup>> elementToGroupLiveData) {
        Observer<List<ElementToGroup>> elementToGroupObserver = elements -> {
            elementToGroupLiveData.removeObservers(owner);
            if (elements.size() == 0) {
                onPreventClosingConsumer.accept(false);
                return;
            }
            int groupId = elements.get(0).getGroupId();
            LiveData<List<Grupo>> grupoLiveData = grupoRepository.findGrupoById(groupId);
            Observer<List<Grupo>> grupoObserver = grupo -> {
                grupoLiveData.removeObservers(owner);
                if (grupo.size() == 0) {
                    onPreventClosingConsumer.accept(false);
                    return;
                }
                boolean isPreventClose = grupo.get(0).isPreventclose();
                onPreventClosingConsumer.accept(isPreventClose);
            };
            grupoLiveData.observe(owner, grupoObserver);
        };
        elementToGroupLiveData.observe(owner, elementToGroupObserver);
    }
}
