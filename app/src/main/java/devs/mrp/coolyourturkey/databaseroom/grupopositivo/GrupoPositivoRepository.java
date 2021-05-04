package devs.mrp.coolyourturkey.databaseroom.grupopositivo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;
import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimit;
import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimitRepository;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExportRepository;

public class GrupoPositivoRepository {

    private GrupoPositivoDao mDao;
    private LiveData<List<GrupoPositivo>> mAllGrupoPositivo;
    private static GrupoPositivoRepository mRepo;
    private GrupoExportRepository exportRepo;
    private GroupLimitRepository limitsRepo;

    private GrupoPositivoRepository(Application application){
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.grupoPositivoDao();
        mAllGrupoPositivo = mDao.findAllGrupoPositivo();
        exportRepo = GrupoExportRepository.getRepo(application);
        limitsRepo = GroupLimitRepository.getRepo(application);
    }

    public static GrupoPositivoRepository getRepo(Application application){
        if (mRepo == null) {
            mRepo = new GrupoPositivoRepository(application);
        }
        return mRepo;
    }

    public void insert(GrupoPositivo grupoPositivo){
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(()-> {
            mDao.insert(grupoPositivo);
        });
    }

    public LiveData<List<GrupoPositivo>> findAllGrupoPositivo() {
        return mAllGrupoPositivo;
    }

    public LiveData<List<GrupoPositivo>> findGrupoPositivoById(Integer id) {
        return mDao.findGrupoPositivoById(id);
    }

    public void deleteById(Integer id){
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(()-> {
            mDao.deleteById(id);
        });
        exportRepo.deleteByGroupId(id);
        limitsRepo.deleteByGroupId(id);
    }

}
