package devs.mrp.coolyourturkey.configuracion.modules;

import android.widget.Switch;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerConfigurerBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.builder.PreferencesSwitchBuilderProvider;
import devs.mrp.coolyourturkey.configuracion.modules.builder.impl.PreferencesSwitchConfigurerBuilder;

@Module(includes = ClickListenerConfigurerProvider.class)
@InstallIn(FragmentComponent.class)
public class UiViewBuilderProvider {

    @Provides
    public PreferencesSwitchBuilderProvider confirmDeactivateSwitchViewBuilder(MisPreferencias preferencias, ClickListenerConfigurerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> switchClickListenerBuilder, DialogWithDelayPresenter dialogWithDelayPresenter) {
        return () -> new PreferencesSwitchConfigurerBuilder(preferencias, switchClickListenerBuilder, dialogWithDelayPresenter);
    }

}
