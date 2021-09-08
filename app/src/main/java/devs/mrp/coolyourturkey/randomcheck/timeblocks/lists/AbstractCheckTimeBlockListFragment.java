package devs.mrp.coolyourturkey.randomcheck.timeblocks.lists;

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
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.CheckTimeBlock;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.TimeBlockWithChecks;
import devs.mrp.coolyourturkey.dtos.timeblock.CheckTimeBlockViewModel;
import devs.mrp.coolyourturkey.dtos.timeblock.TimeBlockFactory;

public abstract class AbstractCheckTimeBlockListFragment<T extends TimeBlockWithChecks> extends Fragment implements MyObservable<T> {
    public static final String CALLBACK_ADD_CONDITION = "callback_add_condition";
    public static final String CALLBACK_CLICK_ELEMENT = "clicked a check from the list";

    protected List<MyObserver<T>> observers = new ArrayList<>();

    protected Context mContext;

    protected CheckTimeBlockViewModel mViewModel;

    protected FloatingActionButton mAddButton;
    protected RecyclerView mRecycler;
    protected TextView mTitle;

    @Override
    public void addObserver(MyObserver<T> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, T feedback) {
        observers.stream().forEach(o -> o.callback(tipo, feedback));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_positive_checks_listing, container, false);

        mAddButton = v.findViewById(R.id.add);
        mRecycler = v.findViewById(R.id.recycler);
        mTitle = v.findViewById(R.id.textView15);
        setTile(mTitle);

        LinearLayoutManager layout = new LinearLayoutManager(mContext);
        mRecycler.setLayoutManager(layout);

        TimeBlockFactory factory = new TimeBlockFactory();
        mViewModel = new CheckTimeBlockViewModel(this.getActivity().getApplication());

        TimeBlockAdapter<T> adapter = new TimeBlockAdapter();
        getTimeBlocks(mViewModel).observe(this, new Observer<List<T>>() {
            @Override
            public void onChanged(List<T> checkTimeBlocks) {
                adapter.updateDataset(checkTimeBlocks);
            }
        });

        adapter.addObserver(new MyObserver<T>() {
            @Override
            public void callback(String tipo, T feedback) {
                if (tipo.equals(TimeBlockAdapter.FEEDBACK_CHECK_SELECTED)) {
                    observers.stream().forEach(o -> o.callback(CALLBACK_CLICK_ELEMENT, feedback));
                }
            }
        });

        mRecycler.setAdapter(adapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCallBack(CALLBACK_ADD_CONDITION, null);
            }
        });

        return v;
    }

    protected abstract LiveData<List<T>> getTimeBlocks(CheckTimeBlockViewModel viewModel);

    protected abstract void setTile(TextView v);
}
