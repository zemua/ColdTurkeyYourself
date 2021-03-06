package devs.mrp.coolyourturkey.databaseroom.grupo.grupoexport;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class GrupoExportRepository {

    private GrupoExportDao mDao;
    private static GrupoExportRepository mRepo;
    private static LiveData<List<GrupoExport>> mAllGrupoExport;

    private GrupoExportRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.grupoExportDao();
        mAllGrupoExport = mDao.findAllGrupoExport();
    }

    public static GrupoExportRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new GrupoExportRepository(application);
        }
        return mRepo;
    }

    public void insert(GrupoExport grupoExport) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.insert(grupoExport);
        });
    }

    public void deleteByGroupId(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteByGroupId(id);
        });
    }

    public LiveData<List<GrupoExport>> findAllGrupoExport() {
        return mAllGrupoExport;
    }

    public LiveData<List<GrupoExport>> findGrupoExportByGroupId(Integer id) {
        return mDao.findGrupoExportByGroupId(id);
    }

}
