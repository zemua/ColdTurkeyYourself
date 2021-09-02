package devs.mrp.coolyourturkey.randomcheck.timeblocks.feedbackchain;

import android.app.Activity;

import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlockRepository;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.review.TimeBlocksFragment;
import devs.mrp.coolyourturkey.watchdog.checkscheduling.CheckManager;

public class BlockFeedbackDeleteHandler extends ChainHandler<AbstractTimeBlock> {

    private Activity mActivity;
    private CheckManager checkManager;

    public BlockFeedbackDeleteHandler(Activity a, CheckManager checkManager) {
        mActivity = a;
        this.checkManager = checkManager;
    }

    @Override
    protected boolean canHandle(String tipo) {
        return tipo.equals(TimeBlocksFragment.FEEDBACK_DELETE_THIS);
    }

    @Override
    protected void handle(AbstractTimeBlock data) {
        CheckTimeBlockRepository repo = CheckTimeBlockRepository.getRepo(mActivity.getApplication());
        repo.deleteById(data.getId(), checkManager);
        mActivity.finish();
    }
}
