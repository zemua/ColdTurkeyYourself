package devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class ConditionNegativeToGroupRepository {

    private ConditionNegativeToGroupDao mDao;
    private LiveData<List<ConditionNegativeToGroup>> mAllConditionToGroup;
    private static ConditionNegativeToGroupRepository mRepo;

    private ConditionNegativeToGroupRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.conditionNegativeToGroupDao();
        mAllConditionToGroup = mDao.findAllConditionToGroup();
    }

    public static ConditionNegativeToGroupRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new ConditionNegativeToGroupRepository(application);
        }
        return mRepo;
    }

    public void insert(ConditionNegativeToGroup condition) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.insert(condition);
        });
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteById(id);
        });
    }

    public void deleteByConditionalGroupId(Integer conditionalGroupId) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteByConditionalGroupId(conditionalGroupId);
        });
    }

    public LiveData<List<ConditionNegativeToGroup>> findAllConditionToGroup() {
        return mAllConditionToGroup;
    }

    public LiveData<List<ConditionNegativeToGroup>> findConditionToGroupById(Integer id) {
        return mDao.findConditionToGroupById(id);
    }

}
