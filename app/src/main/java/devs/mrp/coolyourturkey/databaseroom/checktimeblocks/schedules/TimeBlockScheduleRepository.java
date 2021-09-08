package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.schedules;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class TimeBlockScheduleRepository {

    private TimeBlockScheduleDao mDao;
    private LiveData<List<TimeBlockSchedule>> mAllSchedules;
    private static TimeBlockScheduleRepository mRepo;

    private TimeBlockScheduleRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.timeBlockScheduleDao();
        mAllSchedules = mDao.findAllSchedules();
    }

    public static TimeBlockScheduleRepository getRepo(Application app) {
        if (mRepo == null) {
            mRepo = new TimeBlockScheduleRepository(app);
        }
        return mRepo;
    }

    public void insert(TimeBlockSchedule schedule) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.insert(schedule);
        });
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteScheduleById(id);
        });
    }

    public LiveData<List<TimeBlockSchedule>> findAllSchedules() {
        return mAllSchedules;
    }

    public LiveData<List<TimeBlockSchedule>> findScheduleById(Integer id) {
        return mDao.findScheduleById(id);
    }

}
