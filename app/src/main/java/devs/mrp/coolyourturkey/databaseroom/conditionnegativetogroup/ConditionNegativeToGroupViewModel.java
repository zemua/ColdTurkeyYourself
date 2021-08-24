package devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;

public class ConditionNegativeToGroupViewModel extends AndroidViewModel {

    private ConditionNegativeToGroupRepository mRepo;
    private LiveData<List<ConditionNegativeToGroup>> mAllConditionToGroup;

    public ConditionNegativeToGroupViewModel(@NonNull Application application) {
        super(application);
        mRepo = ConditionNegativeToGroupRepository.getRepo(application);
        mAllConditionToGroup = mRepo.findAllConditionToGroup();
    }

    public void insert(ConditionNegativeToGroup condition) {
        mRepo.insert(condition);
    }

    public void deleteById(Integer id) {
        mRepo.deleteById(id);
    }

    public void deleteByConditionalGroupId(Integer conditionalGroupId) {
        mRepo.deleteByConditionalGroupId(conditionalGroupId);
    }

    public LiveData<List<ConditionNegativeToGroup>> findAllConditionToGroup() {
        return mRepo.findAllConditionToGroup();
    }

    public LiveData<List<ConditionNegativeToGroup>> findConditionToGroupById(Integer id) {
        return mRepo.findConditionToGroupById(id);
    }
}
