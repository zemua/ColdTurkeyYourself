package devs.mrp.coolyourturkey.databaseroom.checktimeblocks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public class CheckTimeBlockViewModel extends AndroidViewModel {

    private CheckTimeBlockRepository mRepo;

    public CheckTimeBlockViewModel(@NonNull Application application) {
        super(application);
        mRepo = CheckTimeBlockRepository.getRepo(application);
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

    public LiveData<List<CheckTimeBlock>> findAllTimeBlocks() {
        return mRepo.findAllTimeBlocks();
    }
}
