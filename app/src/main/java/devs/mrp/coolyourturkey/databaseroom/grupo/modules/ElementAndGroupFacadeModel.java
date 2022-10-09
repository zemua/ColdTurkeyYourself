package devs.mrp.coolyourturkey.databaseroom.grupo.modules;

import android.app.Application;
import android.app.Service;

import androidx.lifecycle.LifecycleService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ServiceComponent;
import devs.mrp.coolyourturkey.databaseroom.grupo.ElementAndGroupFacade;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.impl.ElementAndGroupFacadeImpl;

@Module
@InstallIn(ServiceComponent.class)
public class ElementAndGroupFacadeModel {
    @Provides
    public ElementAndGroupFacade provideElementAndGroupFacade(Service service, Application app) {
        if (service instanceof LifecycleService) {
            LifecycleService lifecycleService = (LifecycleService) service;
            return new ElementAndGroupFacadeImpl(GrupoRepository.getRepo(app), ElementToGroupRepository.getRepo(app), lifecycleService);
        }
        return null;
    }
}
