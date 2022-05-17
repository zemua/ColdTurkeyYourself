package devs.mrp.coolyourturkey.databaseroom.grupocondition;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GrupoConditionViewModel extends AndroidViewModel {

    private GrupoConditionRepository mRepo;
    private LiveData<List<GrupoCondition>> mAllConditions;

    public GrupoConditionViewModel(@NonNull Application application) {
        super(application);
        mRepo = GrupoConditionRepository.getRepo(application);
        mAllConditions = mRepo.findAllConditions();
    }

    public void insert(GrupoCondition grupoCondition) {
        mRepo.insert(grupoCondition);
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

    public LiveData<List<GrupoCondition>> findAllConditions() {
        return mAllConditions;
    }

    public LiveData<List<GrupoCondition>> findConditionById(Integer id) {
        return mRepo.findConditionById(id);
    }

    public LiveData<List<GrupoCondition>> findConditionsByGroupId(Integer groupId) {
        return mRepo.findConditionsByGroupId(groupId);
    }
}
