package devs.mrp.coolyourturkey.databaseroom.elementtogroup;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ElementToGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ElementToGroup element);

    @Query("DELETE FROM elementtogroup")
    void deleteAll();

    @Query("DELETE FROM elementtogroup WHERE id = :id")
    void deleteById(Integer id);

    @Query("DELETE FROM elementtogroup WHERE groupid = :groupid")
    void deleteByGroupId(Integer groupid);

    @Query("DELETE FROM elementtogroup WHERE name = :name AND type = :type")
    void deleteByName(String name, ElementType type);

    @Query("DELETE FROM elementtogroup WHERE toid = :toid AND type = :type")
    void deleteByToId(Long toid, ElementType type);

    @Query("SELECT * FROM elementtogroup ORDER BY id ASC")
    LiveData<List<ElementToGroup>> findAllElementToGroup();

    @Query("SELECT * FROM elementtogroup WHERE id = :id")
    LiveData<List<ElementToGroup>> findElementToGroupById(Integer id);

    @Query("SELECT * FROM elementtogroup WHERE type = :type ORDER BY id ASC")
    LiveData<List<ElementToGroup>> findAllElementsOfType(ElementType type);

}
