package devs.mrp.coolyourturkey.databaseroom.checktimeblocks;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CheckTimeBlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CheckTimeBlock checkTimeBlock);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWithChecks(TimeBlockWithChecks timeBlockWithChecks);

    @Query("DELETE FROM checktimeblock WHERE blockid = :blockid")
    void deleteById(Integer blockid);

    @Query("SELECT * FROM checktimeblock ORDER BY blockid ASC")
    LiveData<List<CheckTimeBlock>> findAllTimeBlocks();

    @Query("SELECT * FROM checktimeblock WHERE blockid = :blockid")
    LiveData<List<CheckTimeBlock>> findTimeBlockById(Integer blockid);

    @Transaction
    @Query("SELECT * FROM checktimeblock WHERE blockid = :blockid")
    LiveData<List<TimeBlockWithChecks>> getTimeBlockWithChecksById(Integer blockid);

}
