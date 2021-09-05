package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class TimeBlockLoggerRepository {

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
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.insert(logger));
    }

    public void deleteByBlockId(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteByBlockId(id));
    }

    LiveData<List<TimeBlockLogger>> findByTimeNewerAndBlockId(Long from, Integer blockid) {
        return mDao.findByTimeNewerAndBlockId(from, blockid);
    }

}
