package devs.mrp.coolyourturkey.randomcheck.timeblocks.listchain;

import android.content.Context;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;

public class BlockListCommander implements ChainComander {

    private Context mContext;

    public BlockListCommander(Context c) {
        mContext = c;
    }

    @Override
    public ChainHandler getHandlerChain() {
        ChainHandler<TimeBlockWithChecks> clickHandler = new BlockClickHandler(mContext);
        ChainHandler<TimeBlockWithChecks> addNewHandler = new BlockListNew(mContext);

        clickHandler.setNextHandler(addNewHandler);

        return clickHandler;
    }
}
