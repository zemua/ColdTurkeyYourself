package devs.mrp.coolyourturkey.databaseroom.grupoexport;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GrupoExportViewModel extends AndroidViewModel {

    private GrupoExportRepository mRepo;

    public GrupoExportViewModel(@NonNull Application application) {
        super(application);
        mRepo = GrupoExportRepository.getRepo(application);
    }

    public void insert(GrupoExport grupoExport) {mRepo.insert(grupoExport);}

    public LiveData<List<GrupoExport>> findGrupoExportByGroupId(Integer groupId) {return mRepo.findGrupoExportByGroupId(groupId);}

    public void deleteByGroupId(Integer groupId) {mRepo.deleteByGroupId(groupId);}
}
