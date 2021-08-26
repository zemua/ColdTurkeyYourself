package devs.mrp.coolyourturkey.randomcheck;

import android.content.Context;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;

public class CheckChainComander implements ChainComander {

    ChainHandler<Context> positiveHandler;

    @Override
    public ChainHandler getHandlerChain() {
        positiveHandler = new CheckChainPositiveChecks();
        ChainHandler<Context> negativeHandler = new CheckChainNegativeChecks();

        positiveHandler.setNextHandler(negativeHandler);

        return positiveHandler;
    }
}
