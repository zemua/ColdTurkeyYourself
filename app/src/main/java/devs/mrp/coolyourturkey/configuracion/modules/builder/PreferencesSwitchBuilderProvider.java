package devs.mrp.coolyourturkey.configuracion.modules.builder;

import android.widget.Switch;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesEnum;

public interface PreferencesSwitchBuilderProvider {

    public ViewConfigurer<MisPreferencias, Switch, PreferencesEnum, Boolean> get();

}
