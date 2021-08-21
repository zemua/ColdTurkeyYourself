package devs.mrp.coolyourturkey.watchdog.actionchain;

public class ActionRequestorFactory implements ActionRequestorFactoryInterface{
    @Override
    public ActionRequestorInterface getChainRequestor() {
        return new ActionRequestor();
    }
}
