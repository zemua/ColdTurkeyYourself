package devs.mrp.coolyourturkey.databaseroom.timelogger;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class TimeLoggerRepository {

    private static TimeLoggerDao mDao;
    private static TimeLoggerRepository mRepo;

    private TimeLoggerRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.timeLoggerDao();
    }

    public static TimeLoggerRepository getRepo(Application application) {
        if (mRepo == null){
            mRepo = new TimeLoggerRepository(application);
        }
        return mRepo;
    }

    public void insert(TimeLogger timeLogger) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() ->{
            mDao.insert(timeLogger);
        });
    }

    public void deleteByEarlierThan(Long millisTimestamp) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteByOlderThanTimestamp(millisTimestamp);
        });
    }

    public LiveData<List<TimeLogger>> findByNewerThanAndGroupId(Long newerThan, Integer groupId) {
        return mDao.findByTimeNewerAndGroupId(newerThan, groupId);
    }

    public LiveData<List<TimeLogger>> findAllTimeLogger() {
        return mDao.findAllTimeLogger();
    }

    public void deleteByGroupId(Integer id) {
        mDao.deleteByGroupId(id);
    }

}
