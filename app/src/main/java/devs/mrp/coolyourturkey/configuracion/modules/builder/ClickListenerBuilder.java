package devs.mrp.coolyourturkey.configuracion.modules.builder;

import android.view.View;

import java.util.Collections;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.function.BiConsumer;

import devs.mrp.coolyourturkey.comun.ClickListenerWithConfirmationFactoryTemplate;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;

public abstract class ClickListenerBuilder<VIEW extends View, REPOSITORY, IDENTIFIER> {

    private REPOSITORY preferencias;
    private DialogWithDelayPresenter dialogWithDelayPresenter;
    private List<View> viewsToModify;
    private BiConsumer<VIEW,View> modifyAction;

    public ClickListenerBuilder<VIEW, REPOSITORY, IDENTIFIER> preferencias(REPOSITORY preferencias) {
        this.preferencias = preferencias;
        return this;
    }

    public ClickListenerBuilder<VIEW, REPOSITORY, IDENTIFIER> dialogWithDelayPresenter(DialogWithDelayPresenter dialogWithDelayPresenter) {
        this.dialogWithDelayPresenter = dialogWithDelayPresenter;
        return this;
    }

    public ClickListenerBuilder<VIEW, REPOSITORY, IDENTIFIER> viewsToModify(List<View> viewsToModify) {
        this.viewsToModify = viewsToModify;
        return this;
    }

    public ClickListenerBuilder<VIEW, REPOSITORY, IDENTIFIER> modifyAction(BiConsumer<VIEW, View> modifyAction) {
        this.modifyAction = modifyAction;
        return this;
    }

    public ClickListenerWithConfirmationFactoryTemplate<VIEW, IDENTIFIER> build() {
        if (viewsToModify == null) {
            viewsToModify = Collections.emptyList();
        }
        if (modifyAction == null) {
            modifyAction = (a,b) -> {};
        }
        if (preferencias == null || dialogWithDelayPresenter == null) {
            throw new RuntimeException("All View builder values must be set");
        }
        return buildListener(preferencias, dialogWithDelayPresenter, viewsToModify, modifyAction);
    }

    protected abstract ClickListenerWithConfirmationFactoryTemplate<VIEW, IDENTIFIER> buildListener(REPOSITORY preferencias,
                                                                                                    DialogWithDelayPresenter dialogWithDelayPresenter,
                                                                                                    List<View> viewsToModify,
                                                                                                    BiConsumer<VIEW,View> modifyAction);

}
