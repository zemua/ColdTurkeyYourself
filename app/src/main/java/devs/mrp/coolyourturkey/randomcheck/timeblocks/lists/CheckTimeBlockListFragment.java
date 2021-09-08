package devs.mrp.coolyourturkey.randomcheck.timeblocks.lists;

import android.widget.TextView;

import androidx.lifecycle.LiveData;

import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlock;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;
import devs.mrp.coolyourturkey.dtos.timeblock.CheckTimeBlockViewModel;

public class CheckTimeBlockListFragment extends AbstractCheckTimeBlockListFragment<TimeBlockWithChecks>{

    @Override
    protected LiveData<List<TimeBlockWithChecks>> getTimeBlocks(CheckTimeBlockViewModel viewModel) {
        return viewModel.getAllTimeBlockWithChecks();
    }

    @Override
    protected void setTile(TextView v) {
        v.setText(R.string.estos_son_tus_bloques_de_tiempo);
    }
}
