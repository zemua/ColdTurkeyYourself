package devs.mrp.coolyourturkey.watchdog.actionchain;

public class ActionRequestor implements ActionRequestorInterface{
    @Override
    public AbstractHandler getHandlerChain() {
        AbstractHandler negative = new NegativeAction();
        AbstractHandler positive = new PositiveAction();
        AbstractHandler neutral = new NeutralAction();
        AbstractHandler nulo = new NullAction();

        negative.setNextHandler(positive);
        positive.setNextHandler(neutral);
        neutral.setNextHandler(nulo);

        return negative;
    }
}
