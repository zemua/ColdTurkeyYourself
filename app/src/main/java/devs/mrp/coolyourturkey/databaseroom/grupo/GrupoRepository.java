package devs.mrp.coolyourturkey.databaseroom.grupo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class GrupoRepository {

    private GrupoDao mDao;
    private LiveData<List<Grupo>> mAllGrupos;
    private LiveData<List<Grupo>> mGruposNegativos;
    private LiveData<List<Grupo>> mGruposPositivos;
    private static GrupoRepository mRepo;

    private GrupoRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.grupoDao();
        mAllGrupos = mDao.findAllGrupos();
        mGruposNegativos = mDao.findGruposByType(GrupoType.NEGATIVE);
        mGruposPositivos = mDao.findGruposByType(GrupoType.POSITIVE);
    }

    public static GrupoRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new GrupoRepository(application);
        }
        return mRepo;
    }

    public void insert(Grupo grupo) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.insert(grupo));
    }

    public LiveData<List<Grupo>> findAllGrupos() {
        return mAllGrupos;
    }

    public LiveData<List<Grupo>> findGrupoById(Integer id) {
        return mDao.findGrupoById(id);
    }

    public LiveData<List<Grupo>> findGruposNegativos() {
        return mGruposNegativos;
    }

    public LiveData<List<Grupo>> findGruposPositivos() {
        return mGruposPositivos;
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteById(id);
            mDao.deleteRelatedExports(id);
            mDao.deleteRelatedAssignations(id);
            mDao.deleteConditionsByThisGroup(id);
            mDao.deleteConditionsByThisTarget(id);
        });
    }

    public void setPreventCloseForGroupId(boolean value, int groupId) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.setPreventCloseForGroupId(value, groupId));
    }

    public void setIgnoreBasedConditionsForGroupId(boolean value, int groupId) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.setIgnoreBasedConditionsForGroupId(value, groupId));
    }

}
