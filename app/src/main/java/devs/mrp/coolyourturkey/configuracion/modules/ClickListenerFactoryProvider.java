package devs.mrp.coolyourturkey.configuracion.modules;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.modules.beans.LockdownNegativesClosedClickListenerFactory;

@Module
@InstallIn(SingletonComponent.class)
public class ClickListenerFactoryProvider {

    @Provides
    @Named("lockdownNegativesAreClosed")
    public ClickListenerWithConfirmationFactoryTemplate lockdownNegativesFactory(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        return new LockdownNegativesClosedClickListenerFactory(preferencias, dialogWithDelayPresenter);
    }

}
