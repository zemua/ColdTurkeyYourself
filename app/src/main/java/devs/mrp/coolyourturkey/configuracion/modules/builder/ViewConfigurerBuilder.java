package devs.mrp.coolyourturkey.configuracion.modules.builder;

import android.view.View;

import java.util.List;
import java.util.function.Supplier;

import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.UiViewConfigurer;

public abstract class ViewConfigurerBuilder<REPOSITORY, VIEW extends View, IDENTIFIER> {

    private REPOSITORY prefs;
    private ClickListenerConfigurerBuilder<VIEW, REPOSITORY, IDENTIFIER> clickListenerFactoryBuilder;
    private DialogWithDelayPresenter dialogWithDelayPresenter;
    private List<Supplier<Boolean>> requiredTrueEnablers;
    private List<Supplier<Boolean>> requiredFalseEnablers;
    private Runnable onStateChangeAction;

    public ViewConfigurerBuilder(REPOSITORY preferencias,
                                 ClickListenerConfigurerBuilder<VIEW, REPOSITORY, IDENTIFIER> clickListenerFactoryBuilder,
                                 DialogWithDelayPresenter dialogWithDelayPresenter) {
        this.prefs = preferencias;
        this.clickListenerFactoryBuilder = clickListenerFactoryBuilder;
        this.dialogWithDelayPresenter = dialogWithDelayPresenter;
    }

    public ViewConfigurerBuilder<REPOSITORY, VIEW, IDENTIFIER> addRequiredTrueEnablers(Supplier<Boolean> trueEnabler) {
        requiredTrueEnablers.add(trueEnabler);
        return this;
    }

    public ViewConfigurerBuilder<REPOSITORY, VIEW, IDENTIFIER> addRequiredFalseEnablers(Supplier<Boolean> falseEnabler) {
        requiredFalseEnablers.add(falseEnabler);
        return this;
    }

    public ViewConfigurerBuilder<REPOSITORY, VIEW, IDENTIFIER> onStateChangeAction(Runnable onStateChangeAction) {
        this.onStateChangeAction = onStateChangeAction;
        return this;
    }

    public UiViewConfigurer<VIEW, IDENTIFIER> configure() {
        // TODO use validation with annotated @NotNull etc class variables
        return configureBuilder(prefs, clickListenerFactoryBuilder, dialogWithDelayPresenter, onStateChangeAction);
    }

    protected abstract UiViewConfigurer<VIEW, IDENTIFIER> configureBuilder(REPOSITORY prefs,
                                                                           ClickListenerConfigurerBuilder<VIEW, REPOSITORY, IDENTIFIER> clickListenerFactoryBuilder,
                                                                           DialogWithDelayPresenter dialogWithDelayPresenter,
                                                                           Runnable onStateChangeAction);

}
