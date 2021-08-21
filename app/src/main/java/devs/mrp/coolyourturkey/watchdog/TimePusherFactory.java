package devs.mrp.coolyourturkey.watchdog;

import devs.mrp.coolyourturkey.databaseroom.contador.ContadorRepository;

public class TimePusherFactory implements TimePusherFactoryInterface{
    @Override
    public TimePusherInterface get(ContadorRepository repo) {
        return new TimePusher(repo);
    }
}
