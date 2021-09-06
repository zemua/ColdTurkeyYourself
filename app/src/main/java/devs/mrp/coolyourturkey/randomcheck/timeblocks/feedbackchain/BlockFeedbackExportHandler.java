package devs.mrp.coolyourturkey.randomcheck.timeblocks.feedbackchain;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.review.TimeBlocksFragment;

public class BlockFeedbackExportHandler extends ChainHandler<AbstractTimeBlock> {

    private Activity mActivity;

    public BlockFeedbackExportHandler(Activity a) {
        mActivity = a;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(TimeBlocksFragment.FEEDBACK_EXPORT_TXT);
    }

    @Override
    protected void handle(AbstractTimeBlock data) {
        // TODO start activity to add export
    }
}
