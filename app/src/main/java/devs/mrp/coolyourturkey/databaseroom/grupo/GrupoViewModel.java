package devs.mrp.coolyourturkey.databaseroom.grupo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GrupoViewModel extends AndroidViewModel {

    private GrupoRepository mRepo;
    private LiveData<List<Grupo>> mAllGrupos;

    public GrupoViewModel(@NonNull Application application) {
        super(application);
        mRepo = GrupoRepository.getRepo(application);
        mAllGrupos = mRepo.findAllGrupoNegativo();
    }

    public void insert(Grupo grupoNegativo) {mRepo.insert(grupoNegativo);}

    public LiveData<List<Grupo>> getAllGrupos() {return mAllGrupos;}

    public LiveData<List<Grupo>> findGrupoNegativoById(Integer id) {
        return mRepo.findGrupoNegativoById(id);
    }

    public void deleteById(Integer id) {mRepo.deleteById(id);}

}
