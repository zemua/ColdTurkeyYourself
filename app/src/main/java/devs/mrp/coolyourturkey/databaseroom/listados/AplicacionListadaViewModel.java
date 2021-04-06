package devs.mrp.coolyourturkey.databaseroom.listados;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AplicacionListadaViewModel extends AndroidViewModel {
    private AplicacionListadaRepository mRepository;

    private LiveData<List<AplicacionListada>> mAllApps;
    private LiveData<List<AplicacionListada>> mAppsPositivasNegativas;
    private LiveData<List<AplicacionListada>> mAppsPositivas;
    private LiveData<List<AplicacionListada>> mAppsNegativas;

    public AplicacionListadaViewModel (Application aplicacion){
        super(aplicacion);
        mRepository = AplicacionListadaRepository.getRepo(aplicacion);
        mAllApps = mRepository.getTodasLasAplicaciones();
        mAppsPositivasNegativas = mRepository.getAppsPositivaNegativa();
        mAppsPositivas = mRepository.getAppsPositivas();
        mAppsNegativas = mRepository.getAPPsNegativas();
    }

    public LiveData<List<AplicacionListada>> getAllApps() {
        return mAllApps;
    }

    public LiveData<List<AplicacionListada>> getmAppsPositivasNegativas() { return mAppsPositivasNegativas; }

    public LiveData<List<AplicacionListada>> getPositiveApps() { return mAppsPositivas; }

    public LiveData<List<AplicacionListada>> getNegativeApps() { return mAppsNegativas; }

    public void insert(AplicacionListada app) {
        mRepository.insert(app);
    }
}
