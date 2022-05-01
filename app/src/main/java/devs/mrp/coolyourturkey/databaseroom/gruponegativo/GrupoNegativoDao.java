package devs.mrp.coolyourturkey.databaseroom.gruponegativo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GrupoNegativoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(GrupoNegativo grupoNegativo);

    @Query("DELETE FROM gruponegativo WHERE id = :id")
    void deleteById(Integer id);

    @Query("SELECT * FROM gruponegativo ORDER BY id ASC")
    LiveData<List<GrupoNegativo>> findAllGrupoNegativo();

    @Query("SELECT * FROM gruponegativo WHERE id = :id")
    LiveData<List<GrupoNegativo>> findGrupoNegativoById(Integer id);

}
