package devs.mrp.coolyourturkey.databaseroom.deprecated.grupopositivo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GrupoPositivoViewModel extends AndroidViewModel {

    private GrupoPositivoRepository mRepo;
    private LiveData<List<GrupoPositivo>> mAllGrupos;

    public GrupoPositivoViewModel(@NonNull Application application) {
        super(application);
        mRepo = GrupoPositivoRepository.getRepo(application);
        mAllGrupos = mRepo.findAllGrupoPositivo();
    }

    public void insert(GrupoPositivo grupoPositivo) {
        mRepo.insert(grupoPositivo);
    }

    public LiveData<List<GrupoPositivo>> getAllGrupos() {
        return mAllGrupos;
    }

    public LiveData<List<GrupoPositivo>> findGrupoPositivoById(Integer id) {
        return mRepo.findGrupoPositivoById(id);
    }

    public void deleteById(Integer id) {
        mRepo.deleteById(id);
    }
}
