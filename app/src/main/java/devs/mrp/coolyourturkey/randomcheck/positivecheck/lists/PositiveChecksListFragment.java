package devs.mrp.coolyourturkey.randomcheck.positivecheck.lists;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheck;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheckRepository;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.randomcheck.AbstractCheckListFragment;
import devs.mrp.coolyourturkey.randomcheck.CheckListAdapter;

public class PositiveChecksListFragment extends AbstractCheckListFragment<PositiveCheck> {

    @Override
    protected List<PositiveCheck> getCheckFromExisting(List<RandomCheck> rcs) {
        return new CheckFactory().positiveFrom(rcs);
    }

    @Override
    protected String getColor() {
        return CheckListAdapter.BACKGROUND_GREEN;
    }

    @Override
    protected LiveData<List<RandomCheck>> getChecks(RandomCheckRepository repo) {
        return repo.getPositiveChecks();
    }

    @Override
    protected void setTile(TextView v) {
        v.setText(R.string.estos_son_tus_positive_checks);
    }
}
