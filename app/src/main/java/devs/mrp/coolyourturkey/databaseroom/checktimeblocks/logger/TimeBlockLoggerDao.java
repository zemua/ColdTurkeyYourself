package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TimeBlockLoggerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimeBlockLogger logger);

    @Query("DELETE FROM timeblocklogger WHERE blockid = :blockid")
    void deleteByBlockId(Integer blockid);

    @Query("SELECT * FROM timeblocklogger ORDER BY epoch ASC")
    LiveData<List<TimeBlockLogger>> findAll();

    @Query("SELECT * FROM timeblocklogger WHERE epoch >= :from AND blockid = :blockid")
    LiveData<List<TimeBlockLogger>> findByTimeNewerAndBlockId(Long from, Integer blockid);

}
