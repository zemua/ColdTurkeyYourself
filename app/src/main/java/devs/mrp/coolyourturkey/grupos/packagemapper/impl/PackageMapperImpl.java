package devs.mrp.coolyourturkey.grupos.packagemapper.impl;

import androidx.lifecycle.LifecycleOwner;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageMapper;

public class PackageMapperImpl implements PackageMapper {

    private ElementToGroupRepository repo;
    private LifecycleOwner owner;

    public PackageMapperImpl(ElementToGroupRepository rep, LifecycleOwner owner) {
        this.repo = rep;
        this.owner = owner;
    }

    @Override
    public void groupIdFromPackageName(String packageName, Consumer<Integer> groupId) {
        repo.findElementOfTypeAndName(ElementType.APP, packageName).observe(owner, elements -> {
            if (elements.size() > 0) {
                groupId.accept(elements.get(0).getGroupId());
            } else {
                groupId.accept(-1);
            }
        });
    }
}
