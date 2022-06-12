package devs.mrp.coolyourturkey.databaseroom.deprecated.grupopositivo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;
import devs.mrp.coolyourturkey.databaseroom.deprecated.conditionnegativetogroup.ConditionNegativeToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.deprecated.conditiontogroup_old_deprecated.ConditionToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.deprecated.grouplimit.GroupLimitRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupoexport.GrupoExportRepository;
import devs.mrp.coolyourturkey.databaseroom.timelogger.TimeLoggerRepository;

public class GrupoPositivoRepository {

    private GrupoPositivoDao mDao;
    private LiveData<List<GrupoPositivo>> mAllGrupoPositivo;
    private static GrupoPositivoRepository mRepo;
    private GrupoExportRepository exportRepo;
    private GroupLimitRepository limitsRepo;
    private ConditionToGroupRepository conditionRepo;
    private ConditionNegativeToGroupRepository negativeConditionRepo;
    private TimeLoggerRepository timeLogRepo;

    private GrupoPositivoRepository(Application application){
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.grupoPositivoDao();
        mAllGrupoPositivo = mDao.findAllGrupoPositivo();
        exportRepo = GrupoExportRepository.getRepo(application);
        limitsRepo = GroupLimitRepository.getRepo(application);
        conditionRepo = ConditionToGroupRepository.getRepo(application);
        negativeConditionRepo = ConditionNegativeToGroupRepository.getRepo(application);
        timeLogRepo = TimeLoggerRepository.getRepo(application);
    }

    public static GrupoPositivoRepository getRepo(Application application){
        if (mRepo == null) {
            mRepo = new GrupoPositivoRepository(application);
        }
        return mRepo;
    }

    public void insert(GrupoPositivo grupoPositivo){
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(()-> mDao.insert(grupoPositivo));
    }

    public LiveData<List<GrupoPositivo>> findAllGrupoPositivo() {
        return mAllGrupoPositivo;
    }

    public LiveData<List<GrupoPositivo>> findGrupoPositivoById(Integer id) {
        return mDao.findGrupoPositivoById(id);
    }

    public void deleteById(Integer id){
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(()-> {
            mDao.deleteById(id);
        });
        exportRepo.deleteByGroupId(id);
        limitsRepo.deleteByGroupId(id);
        conditionRepo.deleteByGroupId(id);
        conditionRepo.deleteByConditionalGroupId(id);
        negativeConditionRepo.deleteByConditionalGroupId(id);
        timeLogRepo.deleteByGroupId(id);
    }

}
