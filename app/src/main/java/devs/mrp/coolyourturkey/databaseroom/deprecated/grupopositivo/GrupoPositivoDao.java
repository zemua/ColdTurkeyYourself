package devs.mrp.coolyourturkey.databaseroom.deprecated.grupopositivo;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GrupoPositivoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(GrupoPositivo grupoPositivo);

    @Query("DELETE FROM grupopositivo")
    void deleteAll();

    @Query("DELETE FROM grupopositivo WHERE id = :id")
    void deleteById(Integer id);

    @Query("SELECT * FROM grupopositivo ORDER BY id ASC")
    LiveData<List<GrupoPositivo>> findAllGrupoPositivo();

    @Query("SELECT * FROM grupopositivo WHERE id = :id")
    LiveData<List<GrupoPositivo>> findGrupoPositivoById(Integer id);

}
