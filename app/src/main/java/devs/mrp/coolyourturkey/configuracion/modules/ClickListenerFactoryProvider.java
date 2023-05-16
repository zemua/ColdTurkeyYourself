package devs.mrp.coolyourturkey.configuracion.modules;

import android.widget.Switch;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.ConfirmDeactivateSwitchListenerFactory;

@Module
@InstallIn(SingletonComponent.class)
public class ClickListenerFactoryProvider {

    @Provides
    public ClickListenerWithConfirmationFactoryTemplate<Switch, PreferencesEnum> lockdownNegativesFactory(MisPreferencias preferencias, DialogWithDelayPresenter dialogWithDelayPresenter) {
        return new ConfirmDeactivateSwitchListenerFactory(preferencias, dialogWithDelayPresenter);
    }

}
