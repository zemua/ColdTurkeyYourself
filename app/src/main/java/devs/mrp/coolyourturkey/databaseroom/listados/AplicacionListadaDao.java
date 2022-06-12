package devs.mrp.coolyourturkey.databaseroom.listados;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AplicacionListadaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AplicacionListada applistada);

    @Query("DELETE FROM elementtogroup WHERE name = :packageName")
    void deleteRelationToGroup(String packageName);

    @Query("SELECT * FROM apps_listadas ORDER BY lista ASC")
    LiveData<List<AplicacionListada>> getAppsList();

    @Query("SELECT * FROM apps_listadas WHERE lista = :tipa OR lista = :tipe")
    LiveData<List<AplicacionListada>> getAppsPositivaNegativa(String tipa, String tipe);

    @Query("SELECT * FROM apps_listadas WHERE lista = :tipo")
    LiveData<List<AplicacionListada>> getAppsPorLista(String tipo);
}
