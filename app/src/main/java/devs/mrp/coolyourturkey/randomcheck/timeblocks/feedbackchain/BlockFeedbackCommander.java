package devs.mrp.coolyourturkey.randomcheck.timeblocks.feedbackchain;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.watchdog.checkscheduling.CheckManager;

public class BlockFeedbackCommander implements ChainComander {

    private Activity mActivity;
    private CheckManager checkManager;

    public BlockFeedbackCommander(Activity a, CheckManager checkManager) {
        mActivity = a;
        this.checkManager = checkManager;
    }

    public static ChainHandler<AbstractTimeBlock> get(Activity a, CheckManager checkManager) {
        return new BlockFeedbackCommander(a, checkManager).getHandlerChain();
    }

    @Override
    public ChainHandler getHandlerChain() {
        ChainHandler<AbstractTimeBlock> saveHandler = new BlockFeedbackSaveHandler(mActivity);
        ChainHandler<AbstractTimeBlock> deleteHandler = new BlockFeedbackDeleteHandler(mActivity, checkManager);
        ChainHandler<AbstractTimeBlock> exportHandler = new BlockFeedbackExportHandler(mActivity);

        saveHandler.setNextHandler(deleteHandler);
        deleteHandler.setNextHandler(exportHandler);

        return saveHandler;
    }
}
