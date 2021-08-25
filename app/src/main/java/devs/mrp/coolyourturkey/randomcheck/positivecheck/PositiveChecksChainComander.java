package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import android.content.Context;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;

public class PositiveChecksChainComander implements ChainComander {

    @Override
    public ChainHandler getHandlerChain() {
        ChainHandler<Context> add = new PositiveChecksChainAdd();

        return add;
    }
}
