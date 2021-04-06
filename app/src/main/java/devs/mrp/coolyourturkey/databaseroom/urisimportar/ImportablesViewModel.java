package devs.mrp.coolyourturkey.databaseroom.urisimportar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ImportablesViewModel extends AndroidViewModel {

    ImportablesRepository mRepo;
    LiveData<List<Importables>> mAllImportables;

    public ImportablesViewModel(@NonNull Application application) {
        super(application);
        mRepo = ImportablesRepository.getRepo(application);
        mAllImportables = mRepo.getAllImportables();
    }

    public void insert(Importables importables){
        mRepo.insert(importables);
    }

    public void delete(String uri){
        mRepo.delete(uri);
    }

    public LiveData<List<Importables>> getAllImportables(){
        return mAllImportables;
    }
}
