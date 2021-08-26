package devs.mrp.coolyourturkey.randomcheck.negativecheck;

import android.view.View;

import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.randomcheck.AbstractChecksFragment;

public class NegativeChecksFragment extends AbstractChecksFragment<Check> {

    @Override
    protected void initializeOtherFields(View v) {

    }

    @Override
    protected void doStuffIfNew() {

    }

    @Override
    protected void doStuffIfExisting() {

    }

    @Override
    protected void fillOtherFields(View v) {

    }

    @Override
    protected boolean assertOtherValidFields() {
        return true;
    }

    @Override
    protected Check getNewCheck() {
        return new CheckFactory().newNegative();
    }

    @Override
    protected void setupOtherObservers() {

    }
}
