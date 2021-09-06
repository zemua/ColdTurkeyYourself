package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TimeBlockExportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TimeBlockExport timeBlockExport);

    @Query("DELETE FROM timeblockexport WHERE expblockid = :id")
    void deleteByBlockId(Integer id);

    @Query("SELECT * FROM timeblockexport WHERE expblockid = :id")
    LiveData<List<TimeBlockExport>> findTimeBlockExportByBlockId(Integer id);

    @Query("SELECT * FROM timeblockexport")
    LiveData<List<TimeBlockExport>> findAllTimeBlockExport();

}
