package devs.mrp.coolyourturkey.databaseroom.valuemap;

import android.app.Application;

import androidx.lifecycle.LiveData;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

import java.util.List;

public class ValueMapRepository {

    private ValueMapDao mValueMapDao;
    private LiveData<List<ValueMap>> mValueOf;

    private static ValueMapRepository mRepo;

    public ValueMapRepository(Application application){
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mValueMapDao = db.valueMapDao();
    }

    public static ValueMapRepository getRepo(Application application){
        if (mRepo == null){
            mRepo = new ValueMapRepository(application);
        }
        return mRepo;
    }

    public LiveData<List<ValueMap>> getValueOf(String nombre){
        mValueOf = mValueMapDao.getValueOf(nombre);
        return mValueOf;
    }

    void insert(ValueMap valueMap){
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(()->{
            mValueMapDao.insert(valueMap);
        });
    }
}
