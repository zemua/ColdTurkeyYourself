package devs.mrp.coolyourturkey.commonmodules;

import java.util.function.Supplier;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import devs.mrp.coolyourturkey.comun.ViewDisabler;
import devs.mrp.coolyourturkey.comun.impl.ViewDisablerImpl;

@Module
@InstallIn(FragmentComponent.class)
public class ViewDisablerModule {

    @Provides
    public Supplier<ViewDisabler> viewDisablerSupplier() {
        return () -> new ViewDisablerImpl();
    }

}
