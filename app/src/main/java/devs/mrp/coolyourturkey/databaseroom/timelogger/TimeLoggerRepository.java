package devs.mrp.coolyourturkey.databaseroom.timelogger;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class TimeLoggerRepository {

    private TimeLoggerDao mDao;
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

    public LiveData<List<TimeLogger>> findByOlderThanAndGroupId(Long olderThan, Integer groupId) {
        return mDao.findByTimeOlderAndGroupId(olderThan, groupId);
    }

}
