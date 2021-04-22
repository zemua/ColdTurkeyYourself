package devs.mrp.coolyourturkey.databaseroom.timelogger;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TimeLoggerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TimeLogger timeLogger);

    @Query("DELETE FROM timelogger WHERE id = :id")
    void deleteByID(Long id);

    @Query("DELETE FROM timelogger WHERE packagename = :packagename")
    void deleteByPackagename(String packagename);

    @Query("DELETE FROM timelogger WHERE groupid = :groupid")
    void deleteByGroupId(Integer groupid);

    @Query("DELETE FROM timelogger WHERE millistimestamp >= :init AND millistimestamp <= :end")
    void deleteByTimestampFrame(Long init, Long end);

    @Query("DELETE FROM timelogger WHERE millistimestamp < :beforemillis")
    void deleteByOlderThanTimestamp(Long beforemillis);

    @Query("SELECT * FROM timelogger ORDER BY millistimestamp ASC")
    LiveData<List<TimeLogger>> findAllTimeLogger();

    @Query("SELECT * FROM timelogger WHERE millistimestamp >= :init AND millistimestamp <= :end")
    LiveData<List<TimeLogger>> findByTimestampFrame(Long init, Long end);

    @Query("SELECT * FROM timelogger WHERE millistimestamp >= :fromMillis")
    LiveData<List<TimeLogger>> findByOlderThanTimestamp(Long fromMillis);

    @Query("SELECT * FROM timelogger WHERE millistimestamp <= :upToMillis")
    LiveData<List<TimeLogger>> findByEarlierThanTimestamp(Long upToMillis);

    @Query("SELECT * FROM timelogger WHERE millistimestamp >= :init AND millistimestamp <= :end AND groupid = :groupid")
    LiveData<List<TimeLogger>> findByTimeframeAndGroupId(Long init, Long end, Integer groupid);

    @Query("SELECT * FROM timelogger WHERE millistimestamp >= :from AND groupid = :groupid")
    LiveData<List<TimeLogger>> findByTimeNewerAndGroupId(Long from, Integer groupid);
}
