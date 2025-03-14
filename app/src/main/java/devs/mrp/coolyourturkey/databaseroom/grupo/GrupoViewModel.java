package devs.mrp.coolyourturkey.databaseroom.grupo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GrupoViewModel extends AndroidViewModel {

    private GrupoRepository mRepo;
    private LiveData<List<Grupo>> mAllGrupos;
    private LiveData<List<Grupo>> mGruposNegativos;
    private LiveData<List<Grupo>> mGruposPositivos;

    public GrupoViewModel(@NonNull Application application) {
        super(application);
        mRepo = GrupoRepository.getRepo(application);
        mAllGrupos = mRepo.findAllGrupos();
        mGruposNegativos = mRepo.findGruposNegativos();
        mGruposPositivos = mRepo.findGruposPositivos();
    }

    public void insert(Grupo grupoNegativo) {mRepo.insert(grupoNegativo);}

    public LiveData<List<Grupo>> getAllGrupos() {return mAllGrupos;}

    public LiveData<List<Grupo>> findGrupoById(Integer id) {
        return mRepo.findGrupoById(id);
    }

    public LiveData<List<Grupo>> findAllGruposNegativos() {
        return mGruposNegativos;
    }

    public LiveData<List<Grupo>> findAllGruposPositivos() {
        return mGruposPositivos;
    }

    public void deleteById(Integer id) {mRepo.deleteById(id);}

    public void setPreventCloseForGroupId(boolean value, int groupId) {mRepo.setPreventCloseForGroupId(value, groupId);}

    public void setIgnoreBasedConditionsForGroupId(boolean value, int groupId) {mRepo.setIgnoreBasedConditionsForGroupId(value, groupId);}

}
