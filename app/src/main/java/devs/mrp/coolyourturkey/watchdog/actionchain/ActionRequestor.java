package devs.mrp.coolyourturkey.watchdog.actionchain;

import devs.mrp.coolyourturkey.watchdog.actionchain.impl.PointsUpdaterImpl;

public class ActionRequestor implements ActionRequestorInterface{
    @Override
    public AbstractHandler getHandlerChain() {
        PointsUpdater pointsUpdater = new PointsUpdaterImpl();

        AbstractHandler negative = new NegativeAction(pointsUpdater);
        AbstractHandler positive = new PositiveAction(pointsUpdater);
        AbstractHandler neutral = new NeutralAction(pointsUpdater);
        AbstractHandler nulo = new NullAction(pointsUpdater);

        negative.setNextHandler(positive);
        positive.setNextHandler(neutral);
        neutral.setNextHandler(nulo);

        return negative;
    }
}
