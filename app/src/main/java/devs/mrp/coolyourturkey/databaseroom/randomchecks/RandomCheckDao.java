package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;

@Dao
public interface RandomCheckDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RandomCheck randomCheck);

    @Query("DELETE FROM randomcheck WHERE id = :id")
    void deleteById(Integer id);

    @TypeConverters(CheckTypeConverter.class)
    @Query("SELECT * FROM randomcheck WHERE type = :type ORDER BY id ASC")
    LiveData<List<RandomCheck>> findAllTypeChecks(RandomCheck.CheckType type);

    @Query("SELECT * FROM randomcheck WHERE id = :id")
    LiveData<List<RandomCheck>> findCheckById(Integer id);

}
