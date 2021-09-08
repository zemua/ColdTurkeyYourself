package devs.mrp.coolyourturkey.randomcheck.negativecheck.chainclick;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.comun.TransferWithBinders;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.randomcheck.negativecheck.review.NegativeChecksActivity;
import devs.mrp.coolyourturkey.randomcheck.negativecheck.lists.NegativeCheckListFragment;

public class NegativeChecksChainClick extends ChainHandler<Check> {

    private Context mContext;

    public NegativeChecksChainClick(Context c) {
        mContext = c;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(NegativeCheckListFragment.CALLBACK_CLICK_ELEMENT);
    }

    @Override
    protected void handle(Check data) {
        Intent intent = new Intent(mContext, NegativeChecksActivity.class);
        TransferWithBinders.addToSend(intent, NegativeChecksActivity.KEY_FOR_RECEIVED_CHECK, data);
        mContext.startActivity(intent);
    }
}
