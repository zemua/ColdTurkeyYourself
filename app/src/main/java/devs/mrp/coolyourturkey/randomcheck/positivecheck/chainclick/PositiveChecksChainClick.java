package devs.mrp.coolyourturkey.randomcheck.positivecheck.chainclick;

import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.comun.TransferWithBinders;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.ContextAndCheckFacade;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.PositiveChecksActivity;
import devs.mrp.coolyourturkey.randomcheck.positivecheck.PositiveChecksListFragment;

public class PositiveChecksChainClick extends ChainHandler<ContextAndCheckFacade> {
    @Override
    protected boolean canHandle(String tipo) {
        if (tipo.equals(PositiveChecksListFragment.CALLBACK_CLICK_ELEMENT)) {
            return true;
        }
        return false;
    }

    @Override
    protected void handle(ContextAndCheckFacade data) {
        Intent intent = new Intent(data.getContext(), PositiveChecksActivity.class);
        TransferWithBinders.addToSend(intent, PositiveChecksActivity.KEY_FOR_RECEIVED_CHECK, data.getCheck());
        data.getContext().startActivity(intent);
    }
}
