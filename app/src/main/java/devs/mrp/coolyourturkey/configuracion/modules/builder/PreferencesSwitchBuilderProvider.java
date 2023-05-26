package devs.mrp.coolyourturkey.configuracion.modules.builder;

import android.widget.Switch;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;

public interface PreferencesSwitchBuilderProvider {

    public ViewConfigurerBuilder<MisPreferencias, Switch, PreferencesBooleanEnum> get();

}
