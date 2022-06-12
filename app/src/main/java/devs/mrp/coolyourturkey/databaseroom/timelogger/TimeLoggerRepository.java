package devs.mrp.coolyourturkey.databaseroom.timelogger;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import devs.mrp.coolyourturkey.comun.MilisToTime;
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

    public LiveData<List<TimeLogger>> findByNewerThan(Long newerThan) {
        return mDao.findByNewerThan(newerThan);
    }

    public LiveData<List<TimeLogger>> findForDayAndGroupId(LocalDate date, Integer groupId) {
        LocalDateTime initOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atStartOfDay().plusDays(1);
        Long initMilis = MilisToTime.localDateTimeToMillis(initOfDay);
        Long endMilis = MilisToTime.localDateTimeToMillis(endOfDay);
        return mDao.findByTimeframeAndGroupId(initMilis, endMilis, groupId);
    }

    public LiveData<List<TimeLogger>> findAllTimeLogger() {
        return mDao.findAllTimeLogger();
    }

    public void deleteByGroupId(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteByGroupId(id);
        });
    }

}
