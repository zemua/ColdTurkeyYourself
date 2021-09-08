package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TimeBlockExportViewModel extends AndroidViewModel {

    private TimeBlockExportRepository mRepo;

    public TimeBlockExportViewModel(@NonNull Application application) {
        super(application);
        mRepo = TimeBlockExportRepository.getRepo(application);
    }

    public void insert(TimeBlockExport timeBlockExport) {
        mRepo.insert(timeBlockExport);
    }

    public void deleteByBlockId(Integer id) {
        mRepo.deleteByBlockId(id);
    }

    public LiveData<List<TimeBlockExport>> findTimeBlockExportByBlockId(Integer id) {
        return mRepo.findTimeBlockExportByBlockId(id);
    }

    public LiveData<List<TimeBlockExport>> findAllTimeBlockExport() {
        return mRepo.findAllTimeBlockExport();
    }
}
