package devs.mrp.coolyourturkey.configuracion.modules.builder;

import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.UiViewConfigurer;
import devs.mrp.coolyourturkey.comun.ViewDisabler;

public abstract class ViewConfigurerBuilder<REPOSITORY, VIEW extends View, IDENTIFIER> {

    private static final String TAG = ViewConfigurerBuilder.class.getSimpleName();

    @NotNull
    private REPOSITORY prefs;
    @NotNull
    private ClickListenerConfigurerBuilder<VIEW, REPOSITORY, IDENTIFIER> clickListenerFactoryBuilder;
    @NotNull
    private DialogWithDelayPresenter dialogWithDelayPresenter;
    private List<Supplier<Boolean>> requiredTrueEnablers = new LinkedList<>();
    private List<Supplier<Boolean>> requiredFalseEnablers = new LinkedList<>();
    private Runnable onStateChangeAction;
    @NotNull
    private View parentView;
    @NotNull
    private Integer resourceId;
    @NotNull
    private IDENTIFIER identifier;
    private ViewDisabler viewDisabler;
    private Function<VIEW,Boolean> conditionForNegative;

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

    public ViewConfigurerBuilder<REPOSITORY, VIEW, IDENTIFIER> viewDisabler(ViewDisabler viewDisabler) {
        this.viewDisabler = viewDisabler;
        return this;
    }

    public ViewConfigurerBuilder<REPOSITORY, VIEW, IDENTIFIER> actionOnStateChange(Runnable onStateChangeAction) {
        this.onStateChangeAction = onStateChangeAction;
        return this;
    }

    public ViewConfigurerBuilder<REPOSITORY, VIEW, IDENTIFIER> parentElement(View parentElement) {
        this.parentView = parentElement;
        return this;
    }

    public ViewConfigurerBuilder<REPOSITORY, VIEW, IDENTIFIER> repositoryIdentifier(IDENTIFIER identifier) {
        this.identifier = identifier;
        return this;
    }

    public ViewConfigurerBuilder<REPOSITORY, VIEW, IDENTIFIER> viewResourceId(Integer resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public ViewConfigurerBuilder<REPOSITORY, VIEW, IDENTIFIER> conditionForNegative(Function<VIEW,Boolean> conditionForNegative) {
        this.conditionForNegative = conditionForNegative;
        return this;
    }

    public UiViewConfigurer<VIEW, IDENTIFIER> configure() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<ViewConfigurerBuilder>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throwViolationsError(violations);
        }
        if (isDiablerMisconfigured()) {
            throw new RuntimeException("There are conditions for disabling the view, but no ViewDisabler configured");
        }
        return configureBuilder(prefs,
                clickListenerFactoryBuilder,
                dialogWithDelayPresenter,
                onStateChangeAction,
                parentView,
                resourceId,
                identifier,
                requiredFalseEnablers,
                requiredTrueEnablers,
                viewDisabler,
                conditionForNegative);
    }

    private void throwViolationsError(Set<ConstraintViolation<ViewConfigurerBuilder>> violations) {
        for (ConstraintViolation<ViewConfigurerBuilder> violation : violations) {
            Log.e(TAG, violation.getPropertyPath() + " " + violation.getMessage());
        }
        throw new RuntimeException("Error validating builder properties");
    }

    private boolean isDiablerMisconfigured() {
        return viewDisabler == null && (!requiredTrueEnablers.isEmpty() || !requiredFalseEnablers.isEmpty());
    }

    protected abstract UiViewConfigurer<VIEW, IDENTIFIER> configureBuilder(REPOSITORY prefs,
                                                                           ClickListenerConfigurerBuilder<VIEW, REPOSITORY, IDENTIFIER> clickListenerFactoryBuilder,
                                                                           DialogWithDelayPresenter dialogWithDelayPresenter,
                                                                           Runnable onStateChangeAction,
                                                                           View parentView,
                                                                           Integer resourceId,
                                                                           IDENTIFIER identifier,
                                                                           List<Supplier<Boolean>> requiredFalseEnablers,
                                                                           List<Supplier<Boolean>> requiredTrueEnablers,
                                                                           ViewDisabler viewDisabler,
                                                                           Function<VIEW,Boolean> conditionForNegative);

}
