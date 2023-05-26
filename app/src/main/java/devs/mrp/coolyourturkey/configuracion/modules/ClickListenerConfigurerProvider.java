package devs.mrp.coolyourturkey.configuracion.modules;

import android.widget.Switch;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerConfigurerBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.builder.impl.SwitchClickListenerBuilder;

@Module
@InstallIn(SingletonComponent.class)
public class ClickListenerConfigurerProvider {

    @Provides
    public ClickListenerConfigurerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> switchClickListenerBuilder() {
        return new SwitchClickListenerBuilder();
    }

}
