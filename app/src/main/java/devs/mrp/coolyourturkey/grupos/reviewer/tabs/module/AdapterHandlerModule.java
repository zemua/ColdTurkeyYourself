package devs.mrp.coolyourturkey.grupos.reviewer.tabs.module;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.grupos.reviewer.ReviewerActivity;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AdapterHandlerFactory;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ConditionsTabFragment;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.factory.AdapterHandlerFactoryImpl;

@Module
@InstallIn(ReviewerActivity.class)
public abstract class AdapterHandlerModule {
    @Binds
    public abstract AdapterHandlerFactory<GrupoCondition> bindAdapterHandlerFactory(AdapterHandlerFactoryImpl adapterHandlerFactory);
}
