package devs.mrp.coolyourturkey.databaseroom.valuemap;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ValueMapViewModel extends AndroidViewModel {

    private ValueMapRepository mRepository;

    private LiveData<List<ValueMap>> mValueOf;

    public ValueMapViewModel(Application application){
        super(application);
        mRepository = ValueMapRepository.getRepo(application);
    }

    public LiveData<List<ValueMap>> getValueOf(String nombre){
        return mRepository.getValueOf(nombre);
    }

    public void insert(ValueMap vm){
        mRepository.insert(vm);
    }
}
