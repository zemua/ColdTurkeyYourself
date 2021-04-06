package devs.mrp.coolyourturkey.databaseroom.contador;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ContadorViewModel extends AndroidViewModel {

    private ContadorRepository mRepo;

    private LiveData<List<Contador>> mTodosContadores;
    private LiveData<List<Contador>> mUltimoContador;

    public ContadorViewModel(Application aplicacion){
        super (aplicacion);
        mRepo = ContadorRepository.getRepo(aplicacion);
        mTodosContadores = mRepo.getTodosContadores();
        mUltimoContador = mRepo.getUltimoContador();
    }

    public void insert(Contador contador){
        mRepo.insert(contador);
    }

    public void clearOlderThan(long epoch){
        mRepo.clearOlderThan(epoch);
    }

    public LiveData<List<Contador>> getTodosContadores(){
        return mTodosContadores;
    }

    public LiveData<List<Contador>> getUltimoContador(){
        return mUltimoContador;
    }
}
