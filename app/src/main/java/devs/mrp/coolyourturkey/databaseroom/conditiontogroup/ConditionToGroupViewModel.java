package devs.mrp.coolyourturkey.databaseroom.conditiontogroup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;
import devs.mrp.coolyourturkey.databaseroom.apptogroup.AppToGroupRepository;

public class ConditionToGroupViewModel extends AndroidViewModel {

    private ConditionToGroupRepository mRepo;
    private LiveData<List<ConditionToGroup>> mAllConditionToGroup;

    public ConditionToGroupViewModel(@NonNull Application application) {
        super(application);
        mRepo = ConditionToGroupRepository.getRepo(application);
        mAllConditionToGroup = mRepo.findAllConditionToGroup();
    }

    public void insert(ConditionToGroup conditionToGroup) {
        mRepo.insert(conditionToGroup);
    }

    public void deleteById(Integer id) {
        mRepo.deleteById(id);
    }

    public void deleteByGroupId(Integer groupId) {
        mRepo.deleteByGroupId(groupId);
    }

    public void deleteByConditionalGroupId(Integer conditionalGroupId) {
        mRepo.deleteByConditionalGroupId(conditionalGroupId);
    }

    public LiveData<List<ConditionToGroup>> findAllConditionToGroup() {
        return mRepo.findAllConditionToGroup();
    }

    public LiveData<List<ConditionToGroup>> findConditionToGroupById(Integer id) {
        return mRepo.findConditionToGroupById(id);
    }

    public LiveData<List<ConditionToGroup>> findConditionToGroupByGroupId(Integer groupId) {
        return mRepo.findConditionToGroupByGroupId(groupId);
    }
}
