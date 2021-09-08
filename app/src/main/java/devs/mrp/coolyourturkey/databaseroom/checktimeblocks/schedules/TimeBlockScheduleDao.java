package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.schedules;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TimeBlockScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimeBlockSchedule schedule);

    @Query("DELETE FROM timeblockschedule WHERE scheduleid = :scheduleid")
    void deleteScheduleById(Integer scheduleid);

    @Query("SELECT * FROM timeblockschedule")
    LiveData<List<TimeBlockSchedule>> findAllSchedules();

    @Query("SELECT * FROM timeblockschedule WHERE scheduleid = :scheduleid")
    LiveData<List<TimeBlockSchedule>> findScheduleById(Integer scheduleid);

}
