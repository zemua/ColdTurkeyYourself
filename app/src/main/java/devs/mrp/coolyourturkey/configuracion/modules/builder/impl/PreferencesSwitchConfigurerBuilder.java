package devs.mrp.coolyourturkey.configuracion.modules.builder.impl;

import android.view.View;
import android.widget.Switch;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.UiViewConfigurer;
import devs.mrp.coolyourturkey.comun.ViewDisabler;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.configuracion.PreferencesBooleanEnum;
import devs.mrp.coolyourturkey.configuracion.modules.beans.PreferencesSwitchConfigurer;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ClickListenerConfigurerBuilder;
import devs.mrp.coolyourturkey.configuracion.modules.builder.ViewConfigurerBuilder;

public class PreferencesSwitchConfigurerBuilder extends ViewConfigurerBuilder<MisPreferencias, Switch, PreferencesBooleanEnum> {

    public PreferencesSwitchConfigurerBuilder(MisPreferencias preferencias, ClickListenerConfigurerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> clickListenerFactoryBuilder, DialogWithDelayPresenter dialogWithDelayPresenter) {
        super(preferencias, clickListenerFactoryBuilder, dialogWithDelayPresenter);
    }

    @Override
    protected UiViewConfigurer<Switch, PreferencesBooleanEnum> configureBuilder(MisPreferencias prefs,
                                                                                ClickListenerConfigurerBuilder<Switch, MisPreferencias, PreferencesBooleanEnum> clickListenerFactoryBuilder,
                                                                                DialogWithDelayPresenter dialogWithDelayPresenter,
                                                                                Runnable onStateChangeAction,
                                                                                View parentView,
                                                                                Integer resourceId,
                                                                                PreferencesBooleanEnum preferencesBooleanEnum,
                                                                                List<Supplier<Boolean>> requiredFalseEnablers,
                                                                                List<Supplier<Boolean>> requiredTrueEnablers,
                                                                                ViewDisabler viewDisabler,
                                                                                Function<Switch,Boolean> conditionForNegative) {
        clickListenerFactoryBuilder.dialogWithDelayPresenter(dialogWithDelayPresenter)
                .preferencias(prefs)
                .conditionForNegative(conditionForNegative)
                .onStateChangeAction(onStateChangeAction);
        return new PreferencesSwitchConfigurer(prefs,
                clickListenerFactoryBuilder.build(),
                parentView,
                resourceId,
                preferencesBooleanEnum,
                requiredFalseEnablers,
                requiredTrueEnablers,
                viewDisabler);
    }
}
