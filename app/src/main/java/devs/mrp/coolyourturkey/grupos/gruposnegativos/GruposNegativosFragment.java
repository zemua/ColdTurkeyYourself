package devs.mrp.coolyourturkey.grupos.gruposnegativos;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.gruponegativo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.gruponegativo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.gruponegativo.GrupoType;
import devs.mrp.coolyourturkey.grupos.GruposAdapter;
import devs.mrp.coolyourturkey.grupos.GruposFragment;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class GruposNegativosFragment extends GruposFragment<Grupo> {

    private static String TAG = "GruposNegativosFragment";

    private GrupoRepository grupoRepository;

    @Override
    protected TimeLogHandler returnTimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner) {
        return new TimeLogHandler(context, application, lifecycleOwner);
    }

    @Override
    protected GruposAdapter returnGruposAdapter(List groupList, Context context, TimeLogHandler timeLogHandler) {
        return new GruposNegativosAdapter(groupList, context, timeLogHandler);
    }

    @Override
    public void addGrupoToDb(Grupo grupo) {
        grupo.setType(GrupoType.NEGATIVE);
        getViewModel().insert(grupo);
    }

    @Override
    public void removeGrupoFromDb(Integer id) {
        // TODO remove association from new GroupingRepository
        // TODO remove conditions from new GroupConditioningRepository
        // no need to delete condition refering to this group, because this is negative
        // Delete this group
        if (grupoRepository == null) {
            grupoRepository = GrupoRepository.getRepo(getActivity().getApplication());
        }
        grupoRepository.deleteById(id);
        Log.d(TAG, "deleted the group");
    }
}
