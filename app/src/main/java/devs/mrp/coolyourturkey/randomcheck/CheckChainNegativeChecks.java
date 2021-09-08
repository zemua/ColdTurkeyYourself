package devs.mrp.coolyourturkey.randomcheck;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.randomcheck.negativecheck.lists.NegativeCheckListActivity;

public class CheckChainNegativeChecks extends ChainHandler<Context> {
    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(RandomChecksFragment.FEEDBACK_NEGATIVE_CHECKS);
    }

    @Override
    protected void handle(Context data) {
        Intent intent = new Intent(data, NegativeCheckListActivity.class);
        data.startActivity(intent);
    }
}
