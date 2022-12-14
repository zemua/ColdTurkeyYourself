package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class TimeBlockLoggerRepository {

    private final String TAG = "TimeBlockLoggerRepository";

    private static TimeBlockLoggerDao mDao;
    private static TimeBlockLoggerRepository mRepo;

    private TimeBlockLoggerRepository(Application app) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(app);
        mDao = db.timeBlockLoggerDao();
    }

    public static TimeBlockLoggerRepository getRepo(Application app) {
        if (mRepo == null) {
            mRepo = new TimeBlockLoggerRepository(app);
        }
        return mRepo;
    }

    public void insert(TimeBlockLogger logger) {
        Log.d(TAG, "to insert logger, epoch:" + logger.getEpoch() + " time:" + logger.getTimecounted() + " blockid:" + logger.getBlockid());
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.insert(logger);
        });
    }

    public void deleteByBlockId(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteByBlockId(id));
    }

    public LiveData<List<TimeBlockLogger>> findAll() {
        return mDao.findAll();
    }

    public LiveData<List<TimeBlockLogger>> findByTimeNewerAndBlockId(Long from, Integer blockid) {
        return mDao.findByTimeNewerAndBlockId(from, blockid);
    }

    public LiveData<List<TimeBlockLogger>> findByTimeNewerAndGroupId(Long from, Integer groupId) {
        return mDao.findByTimeNewerAndGroupId(from, groupId);
    }

    public void deleteByEarlierThan(Long millisTimestamp) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteByOlderThanTimestamp(millisTimestamp);
        });
    }

}
