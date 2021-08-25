package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;

public class PositiveChecksChainAdd extends ChainHandler<ContextAndCheckFacade> {
    @Override
    protected boolean canHandle(String tipo) {
        if (tipo.equals(PositiveChecksListFragment.CALLBACK_ADD_CONDITION)) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(ContextAndCheckFacade data) {
        Intent intent = new Intent(data.getContext(), PositiveChecksActivity.class);
        data.getContext().startActivity(intent);
    }
}
