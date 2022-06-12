package devs.mrp.coolyourturkey.databaseroom.deprecated.conditiontogroup_old_deprecated;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class ConditionToGroupRepository {

    private ConditionToGroupDao mDao;
    private LiveData<List<ConditionToGroup>> mAllConditionToGroup;
    private static ConditionToGroupRepository mRepo;

    private ConditionToGroupRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.conditionToGroupDao();
        mAllConditionToGroup = mDao.findAllConditionToGroup();
    }

    public static ConditionToGroupRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new ConditionToGroupRepository(application);
        }
        return mRepo;
    }

    public void insert(ConditionToGroup conditionToGroup) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.insert(conditionToGroup));
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

    public LiveData<List<ConditionToGroup>> findAllConditionToGroup() {
        return mAllConditionToGroup;
    }

    public LiveData<List<ConditionToGroup>> findConditionToGroupById(Integer id) {
        return mDao.findConditionToGroupById(id);
    }

    public LiveData<List<ConditionToGroup>> findConditionToGroupByGroupId(Integer groupId) {
        return mDao.findConditionToGroupByGroupId(groupId);
    }

}
