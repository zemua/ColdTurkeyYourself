package devs.mrp.coolyourturkey.databaseroom.grupo.grupoexport;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GrupoExportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GrupoExport grupoExport);

    @Query("DELETE FROM grupoexport WHERE groupid = :id")
    void deleteByGroupId(Integer id);

    @Query("SELECT * FROM grupoexport WHERE groupid = :id")
    LiveData<List<GrupoExport>> findGrupoExportByGroupId(Integer id);

    @Query("SELECT * FROM grupoexport")
    LiveData<List<GrupoExport>> findAllGrupoExport();

}
