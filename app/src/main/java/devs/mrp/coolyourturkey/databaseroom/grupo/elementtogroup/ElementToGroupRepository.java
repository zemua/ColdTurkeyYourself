package devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;

public class ElementToGroupRepository {

    private ElementToGroupDao mDao;
    private LiveData<List<ElementToGroup>> mAllElementToGroup;
    private static ElementToGroupRepository mRepo;

    private ElementToGroupRepository(Application application) {
        TurkeyDatabaseRoom db = TurkeyDatabaseRoom.getDatabase(application);
        mDao = db.elementToGroupDao();
        mAllElementToGroup = mDao.findAllElementToGroup();
    }

    public static ElementToGroupRepository getRepo(Application application) {
        if (mRepo == null) {
            mRepo = new ElementToGroupRepository(application);
        }
        return mRepo;
    }

    public void insert(ElementToGroup elementToGroup) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.insert(elementToGroup));
    }

    public void deleteById(Integer id) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteById(id));
    }

    public void deleteByIds(List<Integer> ids) {
        ids.forEach(id -> deleteById(id));
    }

    public void deleteByGroupId(Integer groupid) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteByGroupId(groupid));
    }

    public void deleteByName(String name, ElementType type) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteByName(name, type));
    }

    public void deleteByToId(Long toid, ElementType type) {
        TurkeyDatabaseRoom.databaseWriteExecutor.execute(() -> mDao.deleteByToId(toid, type));
    }

    public LiveData<List<ElementToGroup>> findAllElementToGroup() {
        return mAllElementToGroup;
    }

    public LiveData<List<ElementToGroup>> findElementToGroupById(Integer id) {
        return mDao.findElementToGroupById(id);
    }

    public LiveData<List<ElementToGroup>> findElementsOfType(ElementType type) {
        return mDao.findAllElementsOfType(type);
    }

    public LiveData<List<ElementToGroup>> findElementsOfGroupAndType(Integer groupId, ElementType type) {
        return mDao.findElementsOfGroupAndType(groupId, type);
    }

    public LiveData<List<ElementToGroup>> findElementOfTypeAndElementId(ElementType type, Integer toid) {
        return mDao.findElementOfTypeAndElementId(type, toid);
    }

    public LiveData<List<ElementToGroup>> findElementOfTypeAndName(ElementType type, String name) {
        return mDao.findElementOfTypeAndName(type, name);
    }

}
