package devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ElementToGroupViewModel extends AndroidViewModel {

    private ElementToGroupRepository mRepo;
    private LiveData<List<ElementToGroup>> mAllElementToGroup;

    public ElementToGroupViewModel(@NonNull Application application) {
        super(application);
        mRepo = ElementToGroupRepository.getRepo(application);
        mAllElementToGroup = mRepo.findAllElementToGroup();
    }

    public void insert(ElementToGroup elementToGroup) {
        mRepo.insert(elementToGroup);
    }

    public void deleteById(Integer id) {
        mRepo.deleteById(id);
    }

    public void deleteByIds(List<Integer> ids) {
        mRepo.deleteByIds(ids);
    }

    public void deleteByGroupId(Integer id) {
        mRepo.deleteByGroupId(id);
    }

    public void deleteByName(String name, ElementType type) {
        mRepo.deleteByName(name, type);
    }

    public void deleteByToId(Long toid, ElementType type) {
        mRepo.deleteByToId(toid, type);
    }

    public LiveData<List<ElementToGroup>> findAllElementToGroup() {
        return mAllElementToGroup;
    }

    public LiveData<List<ElementToGroup>> findElementToGroupById(Integer id) {
        return mRepo.findElementToGroupById(id);
    }

    public LiveData<List<ElementToGroup>> findElementsOfType(ElementType type) {
        return mRepo.findElementsOfType(type);
    }

    public LiveData<List<ElementToGroup>> findElementsOfGroupAndType(Integer groupId, ElementType type) {
        return mRepo.findElementsOfGroupAndType(groupId, type);
    }
}
