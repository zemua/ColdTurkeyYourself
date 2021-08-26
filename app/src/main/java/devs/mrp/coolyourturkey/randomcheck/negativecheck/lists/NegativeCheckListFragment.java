package devs.mrp.coolyourturkey.randomcheck.negativecheck.lists;

import android.widget.TextView;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheck;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.randomcheck.AbstractCheckListFragment;
import devs.mrp.coolyourturkey.randomcheck.CheckListAdapter;

public class NegativeCheckListFragment extends AbstractCheckListFragment<Check> {
    @Override
    protected List<Check> getCheckFromExisting(List<RandomCheck> rcs) {
        return new CheckFactory().negativeFrom(rcs);
    }

    @Override
    protected String getColor() {
        return CheckListAdapter.BACKGROUND_RED;
    }

    @Override
    protected LiveData<List<RandomCheck>> getChecks(RandomCheckRepository repo) {
        return repo.getNegativeChecks();
    }

    @Override
    protected void setTile(TextView v) {
        v.setText(R.string.estos_son_tus_negative_checks);
    }
}
