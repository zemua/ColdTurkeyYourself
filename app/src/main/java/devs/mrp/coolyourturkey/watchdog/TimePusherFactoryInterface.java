package devs.mrp.coolyourturkey.watchdog;

import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;

public interface TimePusherFactoryInterface {

    public TimePusherInterface get(ContadorRepository repo);

}
