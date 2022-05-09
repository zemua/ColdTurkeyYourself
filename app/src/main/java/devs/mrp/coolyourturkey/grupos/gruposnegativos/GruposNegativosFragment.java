package devs.mrp.coolyourturkey.grupos.gruposnegativos;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.gruponegativo.Grupo;
import devs.mrp.coolyourturkey.grupos.GruposAdapter;
import devs.mrp.coolyourturkey.grupos.GruposFragment;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class GruposNegativosFragment extends GruposFragment<Grupo> {
    @Override
    protected TimeLogHandler returnTimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner) {
        // TODO make TimeLogHandler for negatives?
        return null;
    }

    @Override
    protected GruposAdapter returnGruposAdapter(List groupList, Context context, TimeLogHandler timeLogHandler) {
        return new GruposNegativosAdapter(groupList, context, timeLogHandler);
    }

    @Override
    public void addGrupoToDb(Grupo grupo) {
        getViewModel().insert(grupo);
    }

    @Override
    public void removeGrupoFromDb(Integer id) {
        // TODO
    }
}
