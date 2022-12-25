package devs.mrp.coolyourturkey.commonmodules;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

@Module
@InstallIn(SingletonComponent.class)
public class PreferenciasModule {
    @Provides
    public MisPreferencias bindsPreferencias(Application app){
        return new MisPreferencias(app);
    }
}
