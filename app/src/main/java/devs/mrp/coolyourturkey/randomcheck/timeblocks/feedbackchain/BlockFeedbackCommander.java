package devs.mrp.coolyourturkey.randomcheck.timeblocks.feedbackchain;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public class BlockFeedbackCommander implements ChainComander {

    private Activity mActivity;

    public BlockFeedbackCommander(Activity a) {
        mActivity = a;
    }

    public static ChainHandler<AbstractTimeBlock> get(Activity a) {
        return new BlockFeedbackCommander(a).getHandlerChain();
    }

    @Override
    public ChainHandler getHandlerChain() {
        ChainHandler<AbstractTimeBlock> saveHandler = new BlockFeedbackSaveHandler(mActivity);
        ChainHandler<AbstractTimeBlock> deleteHandler = new BlockFeedbackDeleteHandler(mActivity);

        saveHandler.setNextHandler(deleteHandler);

        return saveHandler;
    }
}
