package devs.mrp.coolyourturkey.configuracion.modules;

import android.app.Dialog;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import devs.mrp.coolyourturkey.comun.ClickListenerFactory;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.modules.beans.LockdownNegativesClosedClickListenerFactory;

@Module
@InstallIn(FragmentComponent.class)
public class ClickListenerFactoryProvider {

    @Provides
    @Named("lockdownNegativesAreClosed")
    public ClickListenerFactory lockdownNegativesFactory(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        return new LockdownNegativesClosedClickListenerFactory(preferencias, dialogWithDelayPresenter);
    }

}
