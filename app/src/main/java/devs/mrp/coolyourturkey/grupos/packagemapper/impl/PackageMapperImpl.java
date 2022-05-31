package devs.mrp.coolyourturkey.grupos.packagemapper.impl;

import android.os.Handler;

import androidx.lifecycle.LifecycleOwner;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageMapper;

public class PackageMapperImpl implements PackageMapper {

    private ElementToGroupRepository repo;
    private LifecycleOwner owner;
    private Handler mainHandler;

    public PackageMapperImpl(ElementToGroupRepository rep, LifecycleOwner owner, Handler mainHandler) {
        this.repo = rep;
        this.owner = owner;
        this.mainHandler = mainHandler;
    }

    @Override
    public void groupIdFromPackageName(String packageName, Consumer<Integer> groupId) {
        mainHandler.post(() -> {
            repo.findElementOfTypeAndName(ElementType.APP, packageName).observe(owner, elements -> {
                if (elements.size() > 0) {
                    groupId.accept(elements.get(0).getGroupId());
                } else {
                    groupId.accept(-1);
                }
            });
        });
    }
}
