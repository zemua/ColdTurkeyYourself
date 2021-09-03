package devs.mrp.coolyourturkey.watchdog;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;

public class TimePusher implements TimePusherInterface{

    private final String TAG = "TimePusher";

    ContadorRepository mRepo;

    public TimePusher(ContadorRepository repo) {
        this.mRepo = repo;
    }

    @Override
    public void push(long epoch, long acumulado) {
        Contador lcontador = new Contador(epoch, acumulado);
        mRepo.insert(lcontador);
    }

    @Override
    public void add(long epoch, long acumulado, LifecycleOwner owner) {
        LiveData<List<Contador>> ld = mRepo.getUltimoContador();
        ld.observe(owner, contadores -> {
            ld.removeObservers(owner);
            Contador contador = contadores.get(0);
            push(epoch, contador.getAcumulado()+acumulado);
        });
    }
}
