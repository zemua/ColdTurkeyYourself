package devs.mrp.coolyourturkey.databaseroom.grupocondition;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GrupoConditionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GrupoCondition grupoCondition);

    @Query("DELETE FROM grupocondition WHERE id = :id")
    void deleteById(Integer id);

    @Query("DELETE FROM grupocondition WHERE groupid = :groupid")
    void deleteByGroupId(Integer groupid);

    @Query("DELETE FROM grupocondition WHERE conditionalgroupid = :conditionalgroupid")
    void deleteByConditionalGroupId(Integer conditionalgroupid);

    @Query("SELECT * FROM grupocondition ORDER BY ID ASC")
    LiveData<List<GrupoCondition>> findAllConditions();

    @Query("SELECT * FROM grupocondition WHERE id = :id")
    LiveData<List<GrupoCondition>> findConditionById(Integer id);

    @Query("SELECT * FROM grupocondition WHERE groupid = :groupid")
    LiveData<List<GrupoCondition>> findConditionsByGroupId(Integer groupid);

}
