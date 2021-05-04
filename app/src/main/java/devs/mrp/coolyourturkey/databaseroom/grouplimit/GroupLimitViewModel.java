package devs.mrp.coolyourturkey.databaseroom.grouplimit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GroupLimitViewModel extends AndroidViewModel {

    private GroupLimitRepository mRepo;

    public GroupLimitViewModel(@NonNull Application application) {
        super(application);
        mRepo = GroupLimitRepository.getRepo(application);
    }

    public void insert(GroupLimit groupLimit) {mRepo.insert(groupLimit);}

    public void deleteById(Integer id) {mRepo.deleteById(id);}

    public void deleteByGroupId(Integer groupId) {mRepo.deleteByGroupId(groupId);}

    public LiveData<List<GroupLimit>> findByGroupId(Integer groupId) {return mRepo.findGroupLimitByGroupId(groupId);}
}
