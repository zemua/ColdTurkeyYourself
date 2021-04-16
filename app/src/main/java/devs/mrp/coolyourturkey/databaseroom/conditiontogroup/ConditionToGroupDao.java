package devs.mrp.coolyourturkey.databaseroom.conditiontogroup;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ConditionToGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ConditionToGroup conditionToGroup);

    @Query("DELETE FROM conditiontogroup WHERE id = :id")
    void deleteById(Integer id);

    @Query("DELETE FROM conditiontogroup WHERE groupid = :groupid")
    void deleteByGroupId(Integer groupid);

    @Query("DELETE FROM conditiontogroup WHERE conditionalgroupid = :conditionalgroupid")
    void deleteByConditionalGroupId(Integer conditionalgroupid);

    @Query("SELECT * FROM conditiontogroup ORDER BY id ASC")
    LiveData<List<ConditionToGroup>> findAllConditionToGroup();

    @Query("SELECT * FROM conditiontogroup WHERE id = :id")
    LiveData<List<ConditionToGroup>> findConditionToGroupById(Integer id);

    @Query("SELECT * FROM conditiontogroup WHERE groupid = :groupId")
    LiveData<List<ConditionToGroup>> findConditionToGroupByGroupId(Integer groupId);

}
