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
    long insert(CheckTimeBlock checkTimeBlock);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TimeBlockAndChecksCrossRef crossRef);

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimeBlockWithChecks timeBlockWithChecks);*/

    @Query("DELETE FROM timeblockandcheckcrossref WHERE blockid = :blockid")
    void deleteAllCheckReferencesOfBlock(Integer blockid);

    @Query("DELETE FROM timeblockexport WHERE expblockid = :blockid")
    void deleteAllExportReferencesOfBlock(Integer blockid);

    @Query("DELETE FROM elementtogroup WHERE toid = :toid")
    void deleteElementsToGroupOfBlock(Integer toid);

    @Query("DELETE FROM timeblockandcheckcrossref WHERE id = :id")
    void deleteAllReferencesToCheck(Integer id);

    @Query("DELETE FROM timeblockandcheckcrossref WHERE blockid = :blockid AND id = :id")
    void deleteCrossReference(Integer blockid, Integer id);

    @Query("DELETE FROM timeblockandcheckcrossref WHERE blockid = :blockid AND id NOT IN (:id)")
    void deleteCrossReferenceNotPresentIn(Integer blockid, List<Integer> id);

    @Query("DELETE FROM checktimeblock WHERE blockid = :blockid")
    void deleteById(Integer blockid);

    @Query("SELECT * FROM checktimeblock ORDER BY blockid ASC")
    LiveData<List<CheckTimeBlock>> findAllTimeBlocks();

    @Query("SELECT * FROM checktimeblock WHERE blockid = :blockid")
    LiveData<List<CheckTimeBlock>> findTimeBlockById(Integer blockid);

    @Transaction
    @Query("SELECT * FROM checktimeblock WHERE blockid = :blockid")
    LiveData<List<TimeBlockWithChecks>> getTimeBlockWithChecksById(Integer blockid);

    @Transaction
    @Query("SELECT * FROM checktimeblock ORDER BY blockid ASC")
    LiveData<List<TimeBlockWithChecks>> getAllTimeBlockWithChecks();

}
