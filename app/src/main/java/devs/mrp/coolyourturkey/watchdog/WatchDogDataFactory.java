package devs.mrp.coolyourturkey.watchdog;

public class WatchDogDataFactory implements WatchDogDataFactoryInterface{
    @Override
    public WatchDogData create() {
        return new WatchDogData();
    }
}
