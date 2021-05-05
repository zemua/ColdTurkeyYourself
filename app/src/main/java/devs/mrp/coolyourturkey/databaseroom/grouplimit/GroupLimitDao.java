package devs.mrp.coolyourturkey.databaseroom.grouplimit;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.security.acl.Group;
import java.util.List;

@Dao
public interface GroupLimitDao {

    @Insert
    void insert(GroupLimit groupLimit);

    @Query("DELETE FROM 'grouplimit' WHERE id = :id")
    void deleteById(Integer id);

    @Query("DELETE FROM 'grouplimit' WHERE groupid = :groupId")
    void deleteByGroupId(Integer groupId);

    @Query("SELECT * FROM 'grouplimit' WHERE groupid = :groupId")
    LiveData<List<GroupLimit>> findByGroupId(Integer groupId);

    @Query("SELECT * FROM 'grouplimit'")
    LiveData<List<GroupLimit>> findAllGroupLimits();

}
