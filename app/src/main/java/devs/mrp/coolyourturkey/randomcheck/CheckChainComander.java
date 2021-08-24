package devs.mrp.coolyourturkey.randomcheck;

import android.content.Context;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;

public class CheckChainComander implements ChainComander {

    ChainHandler<Context> timeBlocks;

    @Override
    public ChainHandler getHandlerChain() {
        timeBlocks = new CheckChainPositiveChecks();

        return timeBlocks;
    }
}
