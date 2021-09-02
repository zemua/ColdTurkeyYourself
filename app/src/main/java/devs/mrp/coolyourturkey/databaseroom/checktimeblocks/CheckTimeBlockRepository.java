package devs.mrp.coolyourturkey.databaseroom.checktimeblocks;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheck;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.TimeBlockFactory;

public class CheckTimeBlockRepository {

    private static final String TAG = "CheckTimeBlockRepository";

    private CheckTimeBlockDao mDao;
    private LiveData<List<CheckTimeBlock>> mAllTimeBlocks;
    private static CheckTimeBlockRepository mRepo;
    private TimeBlockFactory factory;

    private CheckTimeBlockRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.checkTimeBlockDao();
        mAllTimeBlocks = mDao.findAllTimeBlocks();
        factory = new TimeBlockFactory();
    }

    public static CheckTimeBlockRepository getRepo(Application application){
        if (mRepo == null) {
            mRepo = new CheckTimeBlockRepository(application);
        }
        return mRepo;
    }

    public void insert(AbstractTimeBlock block) {
        Log.d(TAG, "recibido para insertar: " + block.toString());
        TimeBlockWithChecks obj = factory.exportFrom(block);
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteCrossReferenceNotPresentIn(block.getId(), obj.getChecks().stream().map(RandomCheck::getId).collect(Collectors.toList()));
            mDao.insert(obj.getTimeBlock());
            obj.getChecks().stream().forEach(c -> {
                Log.d(TAG, "check of block: " + c.toString());
                TimeBlockAndChecksCrossRef crossRef = new TimeBlockAndChecksCrossRef();
                crossRef.setBlockid(obj.getTimeBlock().getBlockid());
                crossRef.setId(c.getId());
                Log.d(TAG, "calling dao to insert cross-reference: " + crossRef.getBlockid() + " " + crossRef.getId());
                mDao.insert(crossRef);
            });
        });
    }

    @Transaction
    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteById(id);
            mDao.deleteAllCheckReferencesOfBlock(id);
        });
    }

    public LiveData<List<TimeBlockWithChecks>> getTimeBlockWithChecksById(Integer blockid) {
        return mDao.getTimeBlockWithChecksById(blockid);
    }

    public LiveData<List<TimeBlockWithChecks>> getAllTimeBlockWithChecks(){
        return mDao.getAllTimeBlockWithChecks();
    }

    public LiveData<List<CheckTimeBlock>> findAllTimeBlocks() {
        return mAllTimeBlocks;
    }

}
