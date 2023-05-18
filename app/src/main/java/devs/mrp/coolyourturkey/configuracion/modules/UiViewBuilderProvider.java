package devs.mrp.coolyourturkey.configuracion.modules;

import android.widget.Switch;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.builder.PreferencesSwitchBuilderProvider;
import devs.mrp.coolyourturkey.configuracion.modules.builder.impl.SwitchViewConfigurer;

@Module(includes = ClickListenerFactoryProvider.class)
@InstallIn(FragmentComponent.class)
public class UiViewBuilderProvider {

    @Provides
    public PreferencesSwitchBuilderProvider confirmDeactivateSwitchViewBuilder(MisPreferencias preferencias, ClickListenerBuilder<Switch, MisPreferencias, PreferencesEnum> switchClickListenerBuilder, DialogWithDelayPresenter dialogWithDelayPresenter) {
        return () -> new SwitchViewConfigurer(preferencias, switchClickListenerBuilder, dialogWithDelayPresenter);
    }

}
