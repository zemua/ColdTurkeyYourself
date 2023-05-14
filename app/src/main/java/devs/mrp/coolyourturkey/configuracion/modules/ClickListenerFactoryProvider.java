package devs.mrp.coolyourturkey.configuracion.modules;

import android.widget.Switch;

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
    @Named("lockdownNegativesAreClosedListenerFactory")
    public ClickListenerWithConfirmationFactoryTemplate<Switch> lockdownNegativesFactory(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        return new LockdownNegativesClosedClickListenerFactory(preferencias, dialogWithDelayPresenter);
    }

}
