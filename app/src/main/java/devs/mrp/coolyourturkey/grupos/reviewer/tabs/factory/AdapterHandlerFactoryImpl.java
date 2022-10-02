package devs.mrp.coolyourturkey.grupos.reviewer.tabs.factory;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.grupos.GroupType;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AdapterHandler;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.AdapterHandlerFactory;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ConditionsAdapterHandlerImpl;

public class AdapterHandlerFactoryImpl implements AdapterHandlerFactory<GrupoCondition> {

    private Context context;
    private Application application;

    @Inject
    AdapterHandlerFactoryImpl(@ActivityContext Context context, Application application){
        this.context = context;
        this.application = application;
    }

    @Override
    public AdapterHandler<GrupoCondition> getHandler(GroupType groupType, LifecycleOwner owner) {
        return new ConditionsAdapterHandlerImpl(context, groupType, owner, application);
    }
}
