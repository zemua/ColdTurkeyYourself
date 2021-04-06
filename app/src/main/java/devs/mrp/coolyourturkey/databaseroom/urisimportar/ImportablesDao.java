package devs.mrp.coolyourturkey.databaseroom.urisimportar;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImportablesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Importables importables);

    @Query("DELETE FROM importables")
    void deleteAll();

    @Query("DELETE FROM importables WHERE uri = :uri")
    void deleteByUri(String uri);

    @Query("SELECT * FROM importables")
    LiveData<List<Importables>> getImportables();
}
