package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class TimeBlockExportRepository {

    private TimeBlockExportDao mDao;
    private static TimeBlockExportRepository mRepo;
    private static LiveData<List<TimeBlockExport>> mAllBlockExport;

    private TimeBlockExportRepository(Application app) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(app);
        mDao = db.timeBlockExportDao();
        mAllBlockExport = mDao.findAllTimeBlockExport();
    }

    public static TimeBlockExportRepository getRepo(Application app) {
        if (mRepo == null) {
            mRepo = new TimeBlockExportRepository(app);
        }
        return mRepo;
    }

    public void insert(TimeBlockExport timeBlockExport) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.insert(timeBlockExport));
    }

    public void deleteByBlockId(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteByBlockId(id));
    }

    public LiveData<List<TimeBlockExport>> findTimeBlockExportByBlockId(Integer id) {
        return mDao.findTimeBlockExportByBlockId(id);
    }

    public LiveData<List<TimeBlockExport>> findAllTimeBlockExport() {
        return mAllBlockExport;
    }

}
