package devs.mrp.coolyourturkey.randomcheck.negativecheck.chainclick;

import android.content.Context;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;

public class NegativeChecksChainComander implements ChainComander {

    private Context mContext;

    public NegativeChecksChainComander(Context c) {
        mContext = c;
    }

    @Override
    public ChainHandler getHandlerChain() {
        ChainHandler<Check> toAdd = new NegativeChecksChainAdd(mContext);
        ChainHandler<Check> clickHandler = new NegativeChecksChainClick(mContext);

        toAdd.setNextHandler(clickHandler);

        return toAdd;
    }
}
