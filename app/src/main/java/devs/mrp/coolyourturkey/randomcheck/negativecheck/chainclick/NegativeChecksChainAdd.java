package devs.mrp.coolyourturkey.randomcheck.negativecheck.chainclick;

import android.content.Context;
import android.content.Intent;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.randomcheck.negativecheck.NegativeChecksActivity;
import devs.mrp.coolyourturkey.randomcheck.negativecheck.lists.NegativeCheckListFragment;

public class NegativeChecksChainAdd extends ChainHandler<Check> {

    private Context mContext;

    public NegativeChecksChainAdd(Context c) {
        mContext = c;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(NegativeCheckListFragment.CALLBACK_ADD_CONDITION);
    }

    @Override
    protected void handle(Check data) {
        Intent intent = new Intent(mContext, NegativeChecksActivity.class);
        mContext.startActivity(intent);
    }
}
