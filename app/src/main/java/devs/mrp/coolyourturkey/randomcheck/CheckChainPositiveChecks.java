package devs.mrp.coolyourturkey.randomcheck;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.PositiveChecksActivity;

public class CheckChainPositiveChecks extends ChainHandler<Context> {
    @Override
    protected boolean canHandle(String tipo) {
        if (tipo.equals(RandomChecksFragment.FEEDBACK_POSITIVE_CHECKS)) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(Context data) {
        Intent intent = new Intent(data, PositiveChecksActivity.class);
        data.startActivity(intent);
    }
}
