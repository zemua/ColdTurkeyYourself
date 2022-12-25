package devs.mrp.coolyourturkey.commonmodules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import devs.mrp.coolyourturkey.comun.ObjectTransporter;
import devs.mrp.coolyourturkey.comun.impl.ObjectTransporterImpl;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ObjectTransporterModule {
    @Binds
    public abstract ObjectTransporter bindObjectTransporter(ObjectTransporterImpl objectTransporter);
}
