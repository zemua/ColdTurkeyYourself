package devs.mrp.coolyourturkey.databaseroom.apptogroup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AppToGroupViewModel extends AndroidViewModel {

    private AppToGroupRepository mRepo;
    private LiveData<List<AppToGroup>> mAllAppToGroup;

    public AppToGroupViewModel(@NonNull Application application) {
        super(application);
        mRepo = AppToGroupRepository.getRepo(application);
        mAllAppToGroup = mRepo.findAllAppToGroup();
    }

    public void insert(AppToGroup appToGroup) {
        mRepo.insert(appToGroup);
    }

    public  LiveData<List<AppToGroup>> getAllAppToGroup() {
        return mAllAppToGroup;
    }

}
