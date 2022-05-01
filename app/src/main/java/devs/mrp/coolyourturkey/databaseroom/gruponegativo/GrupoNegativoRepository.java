package devs.mrp.coolyourturkey.databaseroom.gruponegativo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;

public class GrupoNegativoRepository {

    private GrupoNegativoDao mDao;
    private LiveData<List<GrupoNegativo>> mAllGrupoNegativo;
    private static GrupoNegativoRepository mRepo;
    // TODO ConditionNegativeToGroupRepository
    private TimeLoggerRepository timeLoggerRepository;

    private GrupoNegativoRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.grupoNegativoDao();
        mAllGrupoNegativo = mDao.findAllGrupoNegativo();
        timeLoggerRepository = TimeLoggerRepository.getRepo(application);
    }

    public static GrupoNegativoRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new GrupoNegativoRepository(application);
        }
        return mRepo;
    }

    public void insert(GrupoNegativo grupoNegativo) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.insert(grupoNegativo));
    }

    public LiveData<List<GrupoNegativo>> findAllGrupoNegativo() {
        return mAllGrupoNegativo;
    }

    public LiveData<List<GrupoNegativo>> findGrupoPositivoById(Integer id) {
        return mDao.findGrupoNegativoById(id);
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteById(id));
        // TODO delete references from other tables like group conditions
    }

}
