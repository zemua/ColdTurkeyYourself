package devs.mrp.coolyourturkey.databaseroom.grupo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GrupoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Grupo grupo);

    @Query("DELETE FROM grupo WHERE id = :id")
    void deleteById(Integer id);

    @Query("DELETE FROM grupoexport WHERE groupid = :groupid")
    void deleteRelatedExports(int groupid);

    @Query("DELETE FROM elementtogroup WHERE groupid = :groupid")
    void deleteRelatedAssignations(int groupid);

    @Query("DELETE FROM grupocondition WHERE groupid = :groupid")
    void deleteConditionsByThisGroup(int groupid);

    @Query("DELETE FROM grupocondition WHERE conditionalgroupid = :conditionalgroupid")
    void deleteConditionsByThisTarget(int conditionalgroupid);

    @Query("SELECT * FROM grupo ORDER BY id ASC")
    LiveData<List<Grupo>> findAllGrupos();

    @Query("SELECT * FROM grupo WHERE id = :id")
    LiveData<List<Grupo>> findGrupoById(Integer id);

   @Query("SELECT * FROM grupo WHERE type = :type ORDER BY id ASC")
   LiveData<List<Grupo>> findGruposByType(GrupoType type);

   @Query("UPDATE grupo SET preventclose = :value WHERE id = :groupId")
    void setPreventCloseForGroupId(boolean value, int groupId);

}
