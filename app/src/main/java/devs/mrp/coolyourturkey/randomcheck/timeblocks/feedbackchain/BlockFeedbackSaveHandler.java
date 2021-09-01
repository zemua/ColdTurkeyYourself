package devs.mrp.coolyourturkey.randomcheck.timeblocks.feedbackchain;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlockRepository;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.review.TimeBlocksFragment;

public class BlockFeedbackSaveHandler extends ChainHandler<AbstractTimeBlock> {

    private Activity mActivity;

    public BlockFeedbackSaveHandler(Activity a) {
        mActivity = a;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(TimeBlocksFragment.FEEDBACK_SAVE_NEW) || tipo.equals(TimeBlocksFragment.FEEDBACK_SAVE_EXISTING);
    }

    @Override
    protected void handle(AbstractTimeBlock data) {
        CheckTimeBlockRepository repo = CheckTimeBlockRepository.getRepo(mActivity.getApplication());
        repo.insert(data);
        mActivity.finish();
    }
}
