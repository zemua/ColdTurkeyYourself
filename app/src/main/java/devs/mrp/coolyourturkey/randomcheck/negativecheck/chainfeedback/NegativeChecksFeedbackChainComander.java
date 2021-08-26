package devs.mrp.coolyourturkey.randomcheck.negativecheck.chainfeedback;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;

public class NegativeChecksFeedbackChainComander implements ChainComander {

    private Activity mActivity;

    public NegativeChecksFeedbackChainComander(Activity a) {
        mActivity = a;
    }

    @Override
    public ChainHandler getHandlerChain() {
        ChainHandler<Check> newHandler = new NegativeCheckFeedbackSaveNew(mActivity);
        ChainHandler<Check> existingHandler = new NegativeCheckFeedbackSaveExisting(mActivity);
        ChainHandler<Check> deleteHandler = new NegativeCheckFeedbackDelete(mActivity);

        newHandler.setNextHandler(existingHandler);
        existingHandler.setNextHandler(deleteHandler);

        return newHandler;
    }
}
