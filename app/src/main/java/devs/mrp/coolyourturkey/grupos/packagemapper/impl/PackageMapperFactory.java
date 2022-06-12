package devs.mrp.coolyourturkey.grupos.packagemapper.impl;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageMapper;

public class PackageMapperFactory {
    public static PackageMapper get(Application app, LifecycleOwner owner) {
        ElementToGroupRepository repo = ElementToGroupRepository.getRepo(app);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        return new PackageMapperImpl(repo, owner, mainHandler);
    }
}
