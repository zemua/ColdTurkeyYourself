package devs.mrp.coolyourturkey.databaseroom.apptogroup;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class AppToGroupRepository {

    private AppToGroupDao mDao;
    private LiveData<List<AppToGroup>> mAllAppToGroup;
    private static AppToGroupRepository mRepo;

    private AppToGroupRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.appToGroupDao();
        mAllAppToGroup = mDao.findAllAppToGroup();
    }

    public static AppToGroupRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new AppToGroupRepository(application);
        }
        return mRepo;
    }

    public void insert(AppToGroup appToGroup) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.insert(appToGroup);
        });
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteById(id);
        });
    }

    public void deleteByIds(List<Integer> ids) {
        ids.forEach(id -> {
            deleteById(id);
        });
    }

    public void deleteByGroupId(Integer groupid) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteByGroupId(groupid);
        });
    }

    public void deleteByPackage(String apppackage) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteByPackage(apppackage);
        });
    }

    public LiveData<List<AppToGroup>> findAllAppToGroup() {
        return mAllAppToGroup;
    }

}
