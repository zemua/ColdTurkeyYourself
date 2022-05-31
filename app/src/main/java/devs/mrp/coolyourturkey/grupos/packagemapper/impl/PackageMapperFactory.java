package devs.mrp.coolyourturkey.grupos.packagemapper.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageMapper;

public class PackageMapperFactory {
    public static PackageMapper get(Application app, LifecycleOwner owner) {
        ElementToGroupRepository repo = ElementToGroupRepository.getRepo(app);
        return new PackageMapperImpl(repo, owner);
    }
}
