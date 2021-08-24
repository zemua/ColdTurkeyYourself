package devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ConditionNegativeToGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ConditionNegativeToGroup conditionNegativeToGroup);

    @Query("DELETE FROM conditionnegativetogroup WHERE id = :id")
    void deleteById(Integer id);

    @Query("DELETE FROM conditionnegativetogroup WHERE conditionalgroupid = :conditionalgroupid")
    void deleteByConditionalGroupId(Integer conditionalgroupid);

    @Query("SELECT * FROM conditionnegativetogroup ORDER BY id ASC")
    LiveData<List<ConditionNegativeToGroup>> findAllConditionToGroup();

    @Query("SELECT * FROM conditionnegativetogroup WHERE id = :id")
    LiveData<List<ConditionNegativeToGroup>> findConditionToGroupById(Integer id);

}
