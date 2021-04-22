package devs.mrp.coolyourturkey.databaseroom.timelogger;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TimeLoggerViewModel extends AndroidViewModel {

    private TimeLoggerRepository mRepo;

    public TimeLoggerViewModel(@NonNull Application application) {
        super(application);
        mRepo = TimeLoggerRepository.getRepo(application);
    }

    public void insert(TimeLogger timeLogger) {mRepo.insert(timeLogger);}

    public void deleteByOlderThan(Long millisOlderThan) {mRepo.deleteByEarlierThan(millisOlderThan);}

    public LiveData<List<TimeLogger>> findByNewerThanAndGroupId(Long newerThan, Integer groupid) {
        return mRepo.findByNewerThanAndGroupId(newerThan, groupid);
    }

}
