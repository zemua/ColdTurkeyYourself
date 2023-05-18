package devs.mrp.coolyourturkey.configuracion.modules.builder.impl;

import android.view.View;
import android.widget.Switch;

import java.util.List;
import java.util.function.BiConsumer;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.ConfirmDeactivateSwitchListenerFactory;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerBuilder;

public class SwitchClickListenerBuilder extends ClickListenerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> {

    @Override
    protected ClickListenerWithConfirmationFactoryTemplate buildListener(MisPreferencias preferencias,
                                                                         DialogWithDelayPresenter dialogWithDelayPresenter,
                                                                         List<View> viewsToModify,
                                                                         BiConsumer<Switch,View> modifyAction) {
        return new ConfirmDeactivateSwitchListenerFactory(preferencias, dialogWithDelayPresenter, viewsToModify, modifyAction);
    }
}
