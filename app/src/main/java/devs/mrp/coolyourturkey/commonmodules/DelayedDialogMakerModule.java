package devs.mrp.coolyourturkey.commonmodules;

import androidx.fragment.app.Fragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import devs.mrp.coolyourturkey.comun.DialogWithDelayAndFragmentResponseFactory;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.impl.DialogWithDelayAndFragmentResponseFactoryImpl;
import devs.mrp.coolyourturkey.comun.impl.DialogWithDelayPresenterImpl;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

@Module
@InstallIn(FragmentComponent.class)
public class DelayedDialogMakerModule {
    @Provides
    public DialogWithDelayPresenter provideDialogWithDelay(Fragment fragment, DialogWithDelayAndFragmentResponseFactory factory, MisPreferencias preferencias) {
        return new DialogWithDelayPresenterImpl(fragment, factory, preferencias.getDelaySeconds());
    }

    @Provides @Named("zeroDelay")
    public DialogWithDelayPresenter provideDialogWithoutDelay(Fragment fragment, DialogWithDelayAndFragmentResponseFactory factory) {
        return new DialogWithDelayPresenterImpl(fragment, factory, 0);
    }

    @Provides
    public DialogWithDelayAndFragmentResponseFactory provideDialogWithDelayFactory() {
        return new DialogWithDelayAndFragmentResponseFactoryImpl();
    }
}
