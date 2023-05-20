package devs.mrp.coolyourturkey.configuracion.modules.builder;

import android.view.View;

import java.util.function.Function;

import devs.mrp.coolyourturkey.comun.ClickListenerConfigurer;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;

public abstract class ClickListenerConfigurerBuilder<VIEW extends View, REPOSITORY, IDENTIFIER> {

    private REPOSITORY preferencias;
    private DialogWithDelayPresenter dialogWithDelayPresenter;
    private Runnable onStateChangeAction;
    private Function<VIEW,Boolean> conditionForNegative;

    public ClickListenerConfigurerBuilder<VIEW, REPOSITORY, IDENTIFIER> preferencias(REPOSITORY preferencias) {
        this.preferencias = preferencias;
        return this;
    }

    public ClickListenerConfigurerBuilder<VIEW, REPOSITORY, IDENTIFIER> dialogWithDelayPresenter(DialogWithDelayPresenter dialogWithDelayPresenter) {
        this.dialogWithDelayPresenter = dialogWithDelayPresenter;
        return this;
    }

    public ClickListenerConfigurerBuilder<VIEW, REPOSITORY, IDENTIFIER> onStateChangeAction(Runnable onStateChangeAction) {
        this.onStateChangeAction = onStateChangeAction;
        return this;
    }

    public ClickListenerConfigurerBuilder<VIEW, REPOSITORY, IDENTIFIER> conditionForNegative(Function<VIEW,Boolean> conditionForNegative) {
        this.conditionForNegative = conditionForNegative;
        return this;
    }

    public ClickListenerConfigurer<VIEW, IDENTIFIER> build() {
        if (anyNulls()) {
            throw new RuntimeException("Preferences and DialogDelayPresenter values must be set");
        }
        if (onStateChangeAction == null) {
            onStateChangeAction = () -> {};
        }
        if (conditionForNegative == null) {
            conditionForNegative = v -> false;
        }
        return buildListener(preferencias, dialogWithDelayPresenter, onStateChangeAction, conditionForNegative);
    }

    private boolean anyNulls() {
        return preferencias == null || dialogWithDelayPresenter == null;
    }

    protected abstract ClickListenerConfigurer<VIEW, IDENTIFIER> buildListener(REPOSITORY preferencias,
                                                                               DialogWithDelayPresenter dialogWithDelayPresenter,
                                                                               Runnable onStateChangeAction,
                                                                               Function<VIEW,Boolean> conditionForNegative);

}
