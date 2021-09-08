package devs.mrp.coolyourturkey.databaseroom.checktimeblocks;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface TimeBlockWithChecksDao {

    @Transaction
    @Query("SELECT * FROM checktimeblock WHERE blockid = :blockid")
    public List<TimeBlockWithChecks> getTimeBlockWithChecksById(Integer blockid);

}
