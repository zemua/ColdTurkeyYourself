package devs.mrp.coolyourturkey.grupos.reviewer.tabs.module;

import android.app.Application;

import androidx.fragment.app.Fragment;;

import javax.inject.Inject;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.components.SingletonComponent;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.grupos.reviewer.ReviewerActivity;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AdapterHandlerFactory;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ConditionsTabFragment;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.factory.AdapterHandlerFactoryImpl;

@Module
@InstallIn(FragmentComponent.class)
public class AdapterHandlerModule {

    @Provides
    public AdapterHandlerFactory<GrupoCondition> bindAdapterHandlerFactory(Fragment fragment, Application application) {
        return new AdapterHandlerFactoryImpl(fragment.getContext(), application);
    }
}
