package devs.mrp.coolyourturkey.databaseroom.contador;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContadorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Contador contador);

    @Query("DELETE FROM contador")
    void deleteAll();

    @Query("DELETE FROM contador WHERE day_of_epoch < :tiempo")
    void deleteOlderThan(long tiempo);

    @Query("SELECT * FROM contador ORDER BY day_of_epoch DESC")
    LiveData<List<Contador>> getContadores();

    @Query("SELECT * FROM contador ORDER BY day_of_epoch DESC LIMIT 1")
    LiveData<List<Contador>> getLastcontador();
}
