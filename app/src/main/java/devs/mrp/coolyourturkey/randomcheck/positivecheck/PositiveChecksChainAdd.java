package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;

public class PositiveChecksChainAdd extends ChainHandler<Context> {
    @Override
    protected boolean canHandle(String tipo) {
        if (tipo.equals(PositiveChecksListFragment.CALLBACK_ADD_CONDITION)) {
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
