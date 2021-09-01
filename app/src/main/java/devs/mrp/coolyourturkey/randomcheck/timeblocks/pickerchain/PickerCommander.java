package devs.mrp.coolyourturkey.randomcheck.timeblocks.pickerchain;

import android.content.Intent;
import android.view.View;

import devs.mrp.coolyourturkey.comun.ChainComander;
import devs.mrp.coolyourturkey.comun.ChainHandler;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.review.TimeBlocksFragment;

public class PickerCommander implements ChainComander {

    private View view;
    private TimeBlocksFragment fr;

    public PickerCommander(View v, TimeBlocksFragment fr) {
        view = v;
        this.fr = fr;
    }

    @Override
    public ChainHandler getHandlerChain() {
        ChainHandler<Intent> fromHandler = new PickerFromHandler(view, fr);
        ChainHandler<Intent> toHandler = new PickerToHandler(view, fr);
        ChainHandler<Intent> minHandler = new PickerMinHandler(view, fr);
        ChainHandler<Intent> maxHandler = new PickerMaxHandler(view, fr);

        fromHandler.setNextHandler(toHandler);
        toHandler.setNextHandler(minHandler);
        minHandler.setNextHandler(maxHandler);

        return fromHandler;
    }
}
