package devs.mrp.coolyourturkey.databaseroom.apptogroup;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppToGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppToGroup appToGroup);

    @Query("DELETE FROM apptogroup")
    void deleteAll();

    @Query("DELETE FROM apptogroup WHERE id = :id")
    void deleteById(Integer id);

    @Query("DELETE FROM apptogroup WHERE groupid = :groupid")
    void deleteByGroupId(Integer groupid);

    @Query("SELECT * FROM apptogroup ORDER BY id ASC")
    LiveData<List<AppToGroup>> findAllAppToGroup();

    @Query("SELECT * FROM apptogroup WHERE id = :id")
    LiveData<List<AppToGroup>> findAppToGroupById(Integer id);

}
