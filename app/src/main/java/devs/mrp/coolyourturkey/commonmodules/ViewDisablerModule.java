package devs.mrp.coolyourturkey.commonmodules;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import devs.mrp.coolyourturkey.comun.ViewDisablerSupplier;
import devs.mrp.coolyourturkey.comun.impl.ViewDisablerImpl;

@Module
@InstallIn(FragmentComponent.class)
public class ViewDisablerModule {

    @Provides
    public ViewDisablerSupplier viewDisablerSupplier() {
        return () -> new ViewDisablerImpl();
    }

}
