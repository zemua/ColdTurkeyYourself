package devs.mrp.coolyourturkey.randomcheck.timeblocks.listchain;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.comun.impl.TransferWithBindersImpl;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;
import devs.mrp.coolyourturkey.dtos.timeblock.TimeBlockFactory;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.lists.AbstractCheckTimeBlockListFragment;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.review.TimeBlocksActivity;

public class BlockClickHandler extends ChainHandler<TimeBlockWithChecks> {

    private Context mContext;

    public BlockClickHandler(Context c) {mContext = c;}

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(AbstractCheckTimeBlockListFragment.CALLBACK_CLICK_ELEMENT);
    }

    @Override
    protected void handle(TimeBlockWithChecks data) {
        Intent intent = new Intent(mContext, TimeBlocksActivity.class);
        TransferWithBindersImpl.addToSend(intent, TimeBlocksActivity.KEY_FOR_RECEIVED_TIME_BLOCK, new TimeBlockFactory().importFrom(data));
        mContext.startActivity(intent);
    }
}
