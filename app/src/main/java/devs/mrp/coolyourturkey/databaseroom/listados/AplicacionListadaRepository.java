package devs.mrp.coolyourturkey.databaseroom.listados;

import android.app.Application;

import androidx.lifecycle.LiveData;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

import java.util.List;

public class AplicacionListadaRepository {

    private static final String TAG = "aplicacion_listada_repository";

    private AplicacionListadaDao mAplicacionListadaDao;
    private LiveData<List<AplicacionListada>> mTodasLasAplicaciones;
    private LiveData<List<AplicacionListada>> mAppsPositivasNegativas;
    private LiveData<List<AplicacionListada>> mAppsPositivas;
    private LiveData<List<AplicacionListada>> mAppsNegativas;
    private static AplicacionListadaRepository mRepo;

    private AplicacionListadaRepository(Application application){
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mAplicacionListadaDao = db.aplicacionListadaDao();
        mTodasLasAplicaciones = mAplicacionListadaDao.getAppsList();
        mAppsPositivasNegativas = mAplicacionListadaDao.getAppsPositivaNegativa(AplicacionListada.getNEGATIVA(), AplicacionListada.getPOSITIVA());
        mAppsPositivas = mAplicacionListadaDao.getAppsPorLista(AplicacionListada.getPOSITIVA());
        mAppsNegativas = mAplicacionListadaDao.getAppsPorLista(AplicacionListada.getNEGATIVA());
    }

    // para acceder a la misma instancia desde el viewholder y desde el servicio
    public static AplicacionListadaRepository getRepo(Application application){
        if (mRepo == null){
            mRepo = new AplicacionListadaRepository(application);
        }
        return mRepo;
    }

    LiveData<List<AplicacionListada>> getTodasLasAplicaciones() {
        return mTodasLasAplicaciones;
    }

    public LiveData<List<AplicacionListada>> getAppsPositivaNegativa() { return mAppsPositivasNegativas; };

    public LiveData<List<AplicacionListada>> getAppsPositivas(){ return mAppsPositivas; }

    public LiveData<List<AplicacionListada>> getAPPsNegativas(){ return mAppsNegativas; }

    void insert(AplicacionListada aplicacion){
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(()->{
            mAplicacionListadaDao.insert(aplicacion);
            if (AplicacionListada.getNEUTRAL().equals(aplicacion.getLista())) {
                mAplicacionListadaDao.deleteRelationToGroup(aplicacion.getNombre());
            }
        });
    }
}
