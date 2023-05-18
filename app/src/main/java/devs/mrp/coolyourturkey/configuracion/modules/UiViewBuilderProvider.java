package devs.mrp.coolyourturkey.configuracion.modules;

import android.widget.Switch;

import java.util.function.Supplier;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ViewConfigurer;
import devs.mrp.coolyourturkey.configuracion.modules.builder.impl.SwitchViewConfigurer;

@Module(includes = ClickListenerFactoryProvider.class)
@InstallIn(FragmentComponent.class)
public class UiViewBuilderProvider {

    @Provides
    public Supplier<ViewConfigurer<MisPreferencias, Switch, PreferencesBooleanEnum, Boolean>> confirmDeactivateSwitchViewBuilder(MisPreferencias preferencias, ClickListenerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> switchClickListenerBuilder, DialogWithDelayPresenter dialogWithDelayPresenter) {
        return () -> new SwitchViewConfigurer(preferencias, switchClickListenerBuilder, dialogWithDelayPresenter);
    }

}
