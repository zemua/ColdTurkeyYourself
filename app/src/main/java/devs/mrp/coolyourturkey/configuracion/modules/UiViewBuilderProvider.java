package devs.mrp.coolyourturkey.configuracion.modules;

import android.widget.Switch;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.UiViewBuilder;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.modules.beans.LockdownNegativesViewBuilder;

@Module
@InstallIn(FragmentComponent.class)
public class UiViewBuilderProvider {

    @Inject
    @Named("lockdownNegativesAreClosed")
    ClickListenerWithConfirmationFactoryTemplate<Switch> clickListenerFactoryProvider;

    @Provides
    @Named("viewBuilderLockdownNegativesAreClosed")
    public UiViewBuilder<Switch> lockdownNegativesViewBuilder(MisPreferencias preferencias) {
        return new LockdownNegativesViewBuilder(preferencias, clickListenerFactoryProvider);
    }

}
