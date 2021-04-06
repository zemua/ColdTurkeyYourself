package devs.mrp.coolyourturkey.databaseroom.contador;

import android.app.Application;

import androidx.lifecycle.LiveData;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

import java.util.List;

public class ContadorRepository {

    private final String TAG = "CONTADOR REPOSITORY CLASS";

    private ContadorDao mContadorDao;
    private static ContadorRepository mRepo;
    private LiveData<List<Contador>> mTodosContadores;
    private LiveData<List<Contador>> mUltimoContador;

    private ContadorRepository(Application aplicacion){
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(aplicacion);
        mContadorDao = db.contadorDao();
        mTodosContadores = mContadorDao.getContadores();
        mUltimoContador = mContadorDao.getLastcontador();
    }

    // porque lo vamos a acceder desde el servicio más que desde el viewholder
    public static ContadorRepository getRepo(Application aplicacion){
        if (mRepo == null){
            mRepo = new ContadorRepository(aplicacion);
        }
        return mRepo;
    }

    public void insert(Contador contador){
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(()->{
            mContadorDao.insert(contador);
        });
    }

    public void clearOlderThan(long epoch){
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(()->{
            mContadorDao.deleteOlderThan(epoch);
        });
    }

    public LiveData<List<Contador>> getTodosContadores(){
        return mTodosContadores;
    }

    public LiveData<List<Contador>> getUltimoContador(){
        return mUltimoContador;
    }
}
