package devs.mrp.coolyourturkey.databaseroom.gruponegativo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;

public class GrupoRepository {

    private GrupoDao mDao;
    private LiveData<List<Grupo>> mAllGrupoNegativo;
    private static GrupoRepository mRepo;
    // TODO ConditionNegativeToGroupRepository
    private TimeLoggerRepository timeLoggerRepository;

    private GrupoRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.grupoDao();
        mAllGrupoNegativo = mDao.findAllGrupoNegativo();
        timeLoggerRepository = TimeLoggerRepository.getRepo(application);
    }

    public static GrupoRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new GrupoRepository(application);
        }
        return mRepo;
    }

    public void insert(Grupo grupoNegativo) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.insert(grupoNegativo));
    }

    public LiveData<List<Grupo>> findAllGrupoNegativo() {
        return mAllGrupoNegativo;
    }

    public LiveData<List<Grupo>> findGrupoNegativoById(Integer id) {
        return mDao.findGrupoNegativoById(id);
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteById(id));
        // TODO delete references from other tables, like group conditions
    }

}
