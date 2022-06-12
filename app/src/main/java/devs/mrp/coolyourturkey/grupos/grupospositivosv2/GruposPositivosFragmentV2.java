package devs.mrp.coolyourturkey.grupos.grupospositivosv2;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoType;
import devs.mrp.coolyourturkey.grupos.GruposAdapter;
import devs.mrp.coolyourturkey.grupos.GruposFragment;
import devs.mrp.coolyourturkey.grupos.conditionchecker.impl.ConditionCheckerFactory;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class GruposPositivosFragmentV2 extends GruposFragment<Grupo> {

    private static String TAG = "GruposPositivosFragment-V2";
    private GrupoRepository grupoRepository;

    @Override
    public void onResume() {
        super.onResume();
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    protected TimeLogHandler returnTimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner) {
        return new TimeLogHandler(context, application, lifecycleOwner);
    }

    @Override
    protected GruposAdapter returnGruposAdapter(List<Grupo> groupList, Context context, TimeLogHandler timeLogHandler) {
        return new GruposPositivosAdapterV2(groupList, context, timeLogHandler, getViewLifecycleOwner(), getActivity().getApplication(), ConditionCheckerFactory.getConditionChecker(getActivity().getApplication(), getViewLifecycleOwner()));
    }

    @Override
    public void addGrupoToDb(Grupo grupo) {
        grupo.setType(GrupoType.POSITIVE);
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

    @Override
    protected LiveData<List<Grupo>> findGrupos() {
        return getViewModel().findAllGruposPositivos();
    }
}
