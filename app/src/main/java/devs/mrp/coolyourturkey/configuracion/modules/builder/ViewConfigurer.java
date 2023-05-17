package devs.mrp.coolyourturkey.configuracion.modules.builder;

import android.view.View;

import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.UiViewBuilder;

public abstract class ViewConfigurer<REPOSITORY, VIEW extends View, IDENTIFIER, DEFAULT> {

    private REPOSITORY prefs;
    private ClickListenerBuilder<VIEW, REPOSITORY, IDENTIFIER> clickListenerFactoryBuilder;
    private DEFAULT defaultState;
    private DialogWithDelayPresenter dialogWithDelayPresenter;
    private List<View> viewsToModify;
    private BiConsumer<VIEW,View> modifyAction;
    private List<Supplier<Boolean>> requiredTrueEnablers;
    private List<Supplier<Boolean>> requiredFalseEnablers;

    public ViewConfigurer(REPOSITORY preferencias,
                          ClickListenerBuilder<VIEW, REPOSITORY, IDENTIFIER> clickListenerFactoryBuilder,
                          DialogWithDelayPresenter dialogWithDelayPresenter) {
        this.prefs = preferencias;
        this.clickListenerFactoryBuilder = clickListenerFactoryBuilder;
        this.dialogWithDelayPresenter = dialogWithDelayPresenter;
    }

    public ViewConfigurer<REPOSITORY, VIEW, IDENTIFIER, DEFAULT> defaultState(DEFAULT defaultState) {
        this.defaultState = defaultState;
        return this;
    }

    public ViewConfigurer<REPOSITORY, VIEW, IDENTIFIER, DEFAULT> viewsToModify(List<View> viewsToModify) {
        this.viewsToModify = viewsToModify;
        return this;
    }

    public ViewConfigurer<REPOSITORY, VIEW, IDENTIFIER, DEFAULT> modifyAction(BiConsumer<VIEW, View> modifyAction) {
        this.modifyAction = modifyAction;
        return this;
    }

    public ViewConfigurer<REPOSITORY, VIEW, IDENTIFIER, DEFAULT> addRequiredTrueEnablers(Supplier<Boolean> trueEnabler) {
        requiredTrueEnablers.add(trueEnabler);
        return this;
    }

    public ViewConfigurer<REPOSITORY, VIEW, IDENTIFIER, DEFAULT> addRequiredFalseEnablers(Supplier<Boolean> falseEnabler) {
        requiredFalseEnablers.add(falseEnabler);
        return this;
    }

    public UiViewBuilder<VIEW, IDENTIFIER> configure() throws InvalidPropertiesFormatException {
        if (defaultState == null) {
            throw new InvalidPropertiesFormatException("Default state should be set");
        }
        return configureBuilder(prefs, clickListenerFactoryBuilder, defaultState, dialogWithDelayPresenter, viewsToModify, modifyAction);
    }

    protected abstract UiViewBuilder<VIEW, IDENTIFIER> configureBuilder(REPOSITORY prefs,
                                                                        ClickListenerBuilder<VIEW, REPOSITORY, IDENTIFIER> clickListenerFactoryBuilder,
                                                                        DEFAULT defaultState,
                                                                        DialogWithDelayPresenter dialogWithDelayPresenter,
                                                                        List<View> viewsToModify,
                                                                        BiConsumer<VIEW,View> modifyAction) throws InvalidPropertiesFormatException;

}
