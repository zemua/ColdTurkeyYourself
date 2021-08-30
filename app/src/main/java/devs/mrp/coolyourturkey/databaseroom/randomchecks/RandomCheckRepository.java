package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;

public class RandomCheckRepository {

    private RandomCheckDao mDao;
    private LiveData<List<RandomCheck>> mPositiveChecks;
    private LiveData<List<RandomCheck>> mNegativeChecks;
    private static RandomCheckRepository mRepo;
    private CheckFactory factory;

    private RandomCheckRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.randomCheckDao();
        mPositiveChecks = mDao.findAllTypeChecks(RandomCheck.CheckType.POSITIVE);
        mNegativeChecks = mDao.findAllTypeChecks(RandomCheck.CheckType.NEGATIVE);
        factory = new CheckFactory();
    }

    public static RandomCheckRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new RandomCheckRepository(application);
        }
        return mRepo;
    }

    public void insertNewPositive(PositiveCheck check) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.insert(factory.exportPositiveFrom(check));
        });
    }

    public void replacePositive(PositiveCheck check) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            try {
                mDao.insert(factory.existingPositiveFrom(check));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void insertNewNegative(Check check) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.insert(factory.newNegativeFrom(check));
        });
    }

    public void replaceNegative(Check check) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            try {
                mDao.insert(factory.existingNegativeFrom(check));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> {
            mDao.deleteById(id);
        });
    }

    public LiveData<List<RandomCheck>> findCheckById(Integer id) {
        return mDao.findCheckById(id);
    }

    public LiveData<List<RandomCheck>> getPositiveChecks() {
        return mPositiveChecks;
    }

    public LiveData<List<RandomCheck>> getNegativeChecks() {
        return mNegativeChecks;
    }

}
