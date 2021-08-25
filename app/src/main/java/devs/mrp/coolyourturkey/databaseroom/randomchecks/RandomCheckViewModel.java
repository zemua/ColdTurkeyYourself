package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.TurkeyDatabaseRoom;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;

public class RandomCheckViewModel extends AndroidViewModel {

    private RandomCheckRepository mRepo;
    private LiveData<List<RandomCheck>> mPositiveChecks;
    private LiveData<List<RandomCheck>> mNegativeChecks;

    public RandomCheckViewModel(@NonNull Application application) {
        super(application);
        mRepo = RandomCheckRepository.getRepo(application);
        mPositiveChecks = mRepo.getPositiveChecks();
        mNegativeChecks = mRepo.getNegativeChecks();
    }

    public void insertNewPositive(PositiveCheck check) {
        mRepo.insertNewPositive(check);
    }

    public void replacePositive(PositiveCheck check) {
        mRepo.replacePositive(check);
    }

    public void insertNewNegative(Check check) {
        mRepo.insertNewNegative(check);
    }

    public void replaceNegative(Check check) {
        mRepo.replaceNegative(check);
    }

    public void deleteById(Integer id) {
        mRepo.deleteById(id);
    }

    public LiveData<List<RandomCheck>> findCheckById(Integer id) {
        return mRepo.findCheckById(id);
    }

    public LiveData<List<RandomCheck>> getPositiveChecks() {
        return mPositiveChecks;
    }

    public LiveData<List<RandomCheck>> getNegativeChecks() {
        return mNegativeChecks;
    }
}
