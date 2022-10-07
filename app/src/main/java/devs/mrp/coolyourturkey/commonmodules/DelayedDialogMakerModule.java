package devs.mrp.coolyourturkey.commonmodules;

import androidx.fragment.app.Fragment;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.components.SingletonComponent;
import devs.mrp.coolyourturkey.comun.DialogWithDelay;
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
    @Provides
    public DialogWithDelayAndFragmentResponseFactory provideDialogWithDelayFactory() {
        return new DialogWithDelayAndFragmentResponseFactoryImpl();
    }
}
