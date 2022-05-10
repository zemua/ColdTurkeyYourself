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

    @Query("SELECT * FROM grupo ORDER BY id ASC")
    LiveData<List<Grupo>> findAllGrupoNegativo();

    @Query("SELECT * FROM grupo WHERE id = :id")
    LiveData<List<Grupo>> findGrupoNegativoById(Integer id);

}
