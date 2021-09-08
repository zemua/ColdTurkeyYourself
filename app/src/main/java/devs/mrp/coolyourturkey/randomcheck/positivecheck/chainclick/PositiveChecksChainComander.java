package devs.mrp.coolyourturkey.randomcheck.positivecheck.chainclick;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.lists.ContextAndCheckFacade;

public class PositiveChecksChainComander implements ChainComander {

    @Override
    public ChainHandler getHandlerChain() {
        ChainHandler<ContextAndCheckFacade> add = new PositiveChecksChainAdd();
        ChainHandler<ContextAndCheckFacade> click = new PositiveChecksChainClick();

        add.setNextHandler(click);

        return add;
    }
}
