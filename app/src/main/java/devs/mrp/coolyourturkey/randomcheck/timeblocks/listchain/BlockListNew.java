package devs.mrp.coolyourturkey.randomcheck.timeblocks.listchain;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.comun.TransferWithBinders;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.lists.AbstractCheckTimeBlockListFragment;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.review.TimeBlocksActivity;

public class BlockListNew extends ChainHandler<TimeBlockWithChecks> {

    private Context mContext;

    public BlockListNew(Context c) {mContext = c;}

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(AbstractCheckTimeBlockListFragment.CALLBACK_ADD_CONDITION);
    }

    @Override
    protected void handle(TimeBlockWithChecks data) {
        Intent intent = new Intent(mContext, TimeBlocksActivity.class);
        mContext.startActivity(intent);
    }
}
