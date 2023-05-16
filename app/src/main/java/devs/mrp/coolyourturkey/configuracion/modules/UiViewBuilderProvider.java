package devs.mrp.coolyourturkey.configuracion.modules;

import android.widget.Switch;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.UiViewBuilder;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.ConfirmDeactivateSwitchViewBuilder;

@Module(includes = ClickListenerFactoryProvider.class)
@InstallIn(FragmentComponent.class)
public class UiViewBuilderProvider {

    @Provides
    public UiViewBuilder<Switch, PreferencesEnum> lockdownNegativesViewBuilder(MisPreferencias preferencias, ClickListenerWithConfirmationFactoryTemplate<Switch, PreferencesEnum> clickListenerFactoryProvider) {
        return new ConfirmDeactivateSwitchViewBuilder(preferencias, clickListenerFactoryProvider, true);
    }

}
