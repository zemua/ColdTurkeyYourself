package devs.mrp.coolyourturkey.watchdog;

public class WatchDogDataFactory implements WatchDogDataFactoryInterface{
    @Override
    public WatchDogData create(WatchdogService service) {
        return new WatchDogData(service);
    }
}
