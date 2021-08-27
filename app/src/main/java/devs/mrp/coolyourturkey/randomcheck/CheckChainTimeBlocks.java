package devs.mrp.coolyourturkey.randomcheck;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.lists.CheckTimeBlockListActivity;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.lists.CheckTimeBlockListFragment;

public class CheckChainTimeBlocks extends ChainHandler<Context> {
    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(RandomChecksFragment.FEEDBACK_TIME_BLOCKS);
    }

    @Override
    protected void handle(Context data) {
        Intent intent = new Intent(data, CheckTimeBlockListActivity.class);
        data.startActivity(intent);
    }
}
