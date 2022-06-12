package devs.mrp.coolyourturkey.databaseroom.deprecated.grouplimit;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class GroupLimitRepository {

    private GroupLimitDao mDao;
    private static GroupLimitRepository mRepo;

    private GroupLimitRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.groupLimitDao();
    }

    public static GroupLimitRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new GroupLimitRepository(application);
        }
        return mRepo;
    }

    public void insert(GroupLimit groupLimit) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.insert(groupLimit);
        });
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteById(id);
        });
    }

    public void deleteByGroupId(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteByGroupId(id);
        });
    }

    public LiveData<List<GroupLimit>> findGroupLimitByGroupId(Integer groupId) {
        return mDao.findByGroupId(groupId);
    }

}
