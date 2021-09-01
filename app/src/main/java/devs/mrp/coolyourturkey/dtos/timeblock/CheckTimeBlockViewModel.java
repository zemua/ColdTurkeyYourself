package devs.mrp.coolyourturkey.dtos.timeblock;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlock;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlockRepository;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;

public class CheckTimeBlockViewModel extends AndroidViewModel {

    private CheckTimeBlockRepository mRepo;
    private LiveData<List<CheckTimeBlock>> mAllTimeBlocks;

    public CheckTimeBlockViewModel(@NonNull Application application) {
        super(application);
        mRepo = CheckTimeBlockRepository.getRepo(application);
        mAllTimeBlocks = mRepo.findAllTimeBlocks();
    }

    public void insert(AbstractTimeBlock block) {
        mRepo.insert(block);
    }

    public void deleteById(Integer id) {
        mRepo.deleteById(id);
    }

    public LiveData<List<TimeBlockWithChecks>> getTimeBlockWithChecksById(Integer blockid) {
        return mRepo.getTimeBlockWithChecksById(blockid);
    }

    public LiveData<List<TimeBlockWithChecks>> getAllTimeBlockWithChecks() {
        return mRepo.getAllTimeBlockWithChecks();
    }

    public LiveData<List<CheckTimeBlock>> findAllTimeBlocks() {
        return mAllTimeBlocks;
    }
}
