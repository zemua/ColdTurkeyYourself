package devs.mrp.coolyourturkey.configuracion.modules;

import android.widget.Switch;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.builder.impl.SwitchClickListenerBuilder;

@Module
@InstallIn(SingletonComponent.class)
public class ClickListenerFactoryProvider {

    @Provides
    public ClickListenerBuilder<Switch, MisPreferencias, PreferencesEnum> switchClickListenerBuilder() {
        return new SwitchClickListenerBuilder();
    }

}
