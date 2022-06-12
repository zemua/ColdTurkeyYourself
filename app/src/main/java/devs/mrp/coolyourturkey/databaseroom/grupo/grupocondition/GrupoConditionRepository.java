package devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class GrupoConditionRepository {

    private GrupoConditionDao mDao;
    private LiveData<List<GrupoCondition>> mAllConditions;
    private static GrupoConditionRepository mRepo;

    private GrupoConditionRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.grupoConditionDao();
        mAllConditions = mDao.findAllConditions();
    }

    public static GrupoConditionRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new GrupoConditionRepository(application);
        }
        return mRepo;
    }

    public void insert(GrupoCondition condition) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.insert(condition));
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteById(id));
    }

    public void deleteByGroupId(Integer groupId) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteByGroupId(groupId));
    }

    public void deleteByConditionalGroupId(Integer conditionalGroupId) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteByConditionalGroupId(conditionalGroupId));
    }

    public LiveData<List<GrupoCondition>> findAllConditions() {
        return mAllConditions;
    }

    public LiveData<List<GrupoCondition>> findConditionById(Integer id) {
        return mDao.findConditionById(id);
    }

    public LiveData<List<GrupoCondition>> findConditionsByGroupId(Integer groupId) {
        return mDao.findConditionsByGroupId(groupId);
    }

}
