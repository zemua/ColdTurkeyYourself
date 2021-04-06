package devs.mrp.coolyourturkey.databaseroom.urisimportar;

import android.app.Application;

import androidx.lifecycle.LiveData;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

import java.util.List;

public class ImportablesRepository {

    private static ImportablesRepository mRepo;
    private ImportablesDao mImportablesDao;
    private LiveData<List<Importables>> mAllImportables;

    private ImportablesRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mImportablesDao = db.importablesDao();
        mAllImportables = mImportablesDao.getImportables();
    }

    // porque también lo vamos a acceder desde el servicio, para añadir tiempo de otros equipos
    public static ImportablesRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new ImportablesRepository(application);
        }
        return mRepo;
    }

    public void insert(Importables importables) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mImportablesDao.insert(importables);
        });
    }

    public void delete(String uri) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mImportablesDao.deleteByUri(uri);
        });
    }

    public LiveData<List<Importables>> getAllImportables() {
        return mAllImportables;
    }
}
