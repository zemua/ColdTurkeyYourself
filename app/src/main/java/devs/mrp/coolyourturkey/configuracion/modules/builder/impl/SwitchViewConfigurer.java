package devs.mrp.coolyourturkey.configuracion.modules.builder.impl;

import android.view.View;
import android.widget.Switch;

import java.util.List;
import java.util.function.BiConsumer;

import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.UiViewBuilder;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.ConfirmDeactivateSwitchViewBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ViewConfigurer;

public class SwitchViewConfigurer extends ViewConfigurer<MisPreferencias, Switch, PreferencesBooleanEnum, Boolean> {

    public SwitchViewConfigurer(MisPreferencias preferencias, ClickListenerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> clickListenerFactoryBuilder, DialogWithDelayPresenter dialogWithDelayPresenter) {
        super(preferencias, clickListenerFactoryBuilder, dialogWithDelayPresenter);
    }

    @Override
    protected UiViewBuilder<Switch, PreferencesBooleanEnum> configureBuilder(MisPreferencias prefs,
                                                                             ClickListenerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> clickListenerFactoryBuilder,
                                                                             Boolean defaultState,
                                                                             DialogWithDelayPresenter dialogWithDelayPresenter,
                                                                             List<View> viewsToModify,
                                                                             BiConsumer<Switch, View> modifyAction) {
        clickListenerFactoryBuilder.dialogWithDelayPresenter(dialogWithDelayPresenter)
                .modifyAction(modifyAction)
                .preferencias(prefs)
                .viewsToModify(viewsToModify);
        return new ConfirmDeactivateSwitchViewBuilder(prefs, clickListenerFactoryBuilder.build(), defaultState);
    }
}
