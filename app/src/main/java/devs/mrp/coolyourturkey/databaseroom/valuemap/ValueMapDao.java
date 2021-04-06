package devs.mrp.coolyourturkey.databaseroom.valuemap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ValueMapDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ValueMap valueMap);

    @Query("DELETE FROM value_map")
    void deleteAll();

    @Query("SELECT * FROM value_map")
    LiveData<List<ValueMap>> getValuesList();

    @Query("SELECT * FROM value_map WHERE nombre = :name")
    LiveData<List<ValueMap>> getValueOf(String name);
}
