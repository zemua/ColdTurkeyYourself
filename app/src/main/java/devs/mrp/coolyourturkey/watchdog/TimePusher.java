package devs.mrp.coolyourturkey.watchdog;

import devs.mrp.coolyourturkey.databaseroom.contador.Contador;
import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;

public class TimePusher implements TimePusherInterface{

    ContadorRepository mRepo;

    public TimePusher(ContadorRepository repo) {
        this.mRepo = repo;
    }

    @Override
    public void push(long epoch, long acumulado) {
        Contador lcontador = new Contador(epoch, acumulado);
        mRepo.insert(lcontador);
    }
}
