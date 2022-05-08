package devs.mrp.coolyourturkey.databaseroom.gruponegativo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GrupoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Grupo grupoNegativo);

    @Query("DELETE FROM Grupo WHERE id = :id")
    void deleteById(Integer id);

    @Query("SELECT * FROM Grupo ORDER BY id ASC")
    LiveData<List<Grupo>> findAllGrupoNegativo();

    @Query("SELECT * FROM Grupo WHERE id = :id")
    LiveData<List<Grupo>> findGrupoNegativoById(Integer id);

}
