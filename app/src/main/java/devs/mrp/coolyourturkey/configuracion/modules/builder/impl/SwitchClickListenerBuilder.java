package devs.mrp.coolyourturkey.configuracion.modules.builder.impl;

import android.widget.Switch;

import devs.mrp.coolyourturkey.comun.ClickListenerConfigurer;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.PreferencesSwitchListenerConfigurer;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerConfigurerBuilder;

public class SwitchClickListenerBuilder extends ClickListenerConfigurerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> {

    @Override
    protected ClickListenerConfigurer buildListener(MisPreferencias preferencias,
                                                    DialogWithDelayPresenter dialogWithDelayPresenter,
                                                    Runnable onStateChangeAction) {
        return new PreferencesSwitchListenerConfigurer(preferencias, dialogWithDelayPresenter, onStateChangeAction);
    }
}
