package devs.mrp.coolyourturkey.grupos.packagemapper.impl;

import android.os.Handler;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageGroupMapper;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageMapper;

public class PackageGroupMapperImpl implements PackageGroupMapper {

    private PackageMapper packageMapper;
    private Handler mainHandler;
    private GrupoRepository grupoRepository;
    private LifecycleOwner owner;

    public PackageGroupMapperImpl(GrupoRepository repo, PackageMapper mapper, LifecycleOwner owner, Handler mainHandler) {
        this.packageMapper = mapper;
        this.mainHandler = mainHandler;
        this.grupoRepository = repo;
        this.owner = owner;
    }

    @Override
    public void groupNameFromPackageName(String packageName, Consumer<String> groupName) {
        packageMapper.groupIdFromPackageName(packageName, id -> {
            mainHandler.post(() -> {
                LiveData<List<Grupo>> elements = grupoRepository.findGrupoById(id);
                elements.observe(owner, grupos -> {
                    elements.removeObservers(owner);
                    if (grupos.size()>0) {
                        groupName.accept(grupos.get(0).getNombre());
                    } else {
                        groupName.accept("");
                    }
                });
            });
        });
    }
}
