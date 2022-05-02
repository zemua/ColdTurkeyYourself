package devs.mrp.coolyourturkey.databaseroom.gruponegativo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GrupoNegativoViewModel extends AndroidViewModel {

    private GrupoNegativoRepository mRepo;
    private LiveData<List<GrupoNegativo>> mAllGrupos;

    public GrupoNegativoViewModel(@NonNull Application application) {
        super(application);
        mRepo = GrupoNegativoRepository.getRepo(application);
        mAllGrupos = mRepo.findAllGrupoNegativo();
    }

    public void insert(GrupoNegativo grupoNegativo) {mRepo.insert(grupoNegativo);}

    public LiveData<List<GrupoNegativo>> getAllGrupos() {return mAllGrupos;}

    public LiveData<List<GrupoNegativo>> findGrupoNegativoById(Integer id) {
        return mRepo.findGrupoNegativoById(id);
    }

    public void deleteById(Integer id) {mRepo.deleteById(id);}

}
