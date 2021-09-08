package devs.mrp.coolyourturkey.randomcheck;

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
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;

public abstract class AbstractCheckListFragment<T extends Check> extends Fragment implements MyObservable<T> {

    public static final String CALLBACK_ADD_CONDITION = "callback_add_condition";
    public static final String CALLBACK_CLICK_ELEMENT = "clicked a check from the list";

    protected List<MyObserver<T>> observers = new ArrayList<>();

    protected Context mContext;

    protected RandomCheckRepository mRepo;

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

        CheckFactory factory = new CheckFactory();
        mRepo = RandomCheckRepository.getRepo(this.getActivity().getApplication());
        CheckListAdapter<T> adapter = new CheckListAdapter(mContext, getColor());
        getChecks(mRepo).observe(this, new Observer<List<RandomCheck>>() {
            @Override
            public void onChanged(List<RandomCheck> randomChecks) {
                List<T> checks = getCheckFromExisting(randomChecks);
                adapter.updateDataset(checks);
            }
        });

        adapter.addObserver(new MyObserver<T>() {
            @Override
            public void callback(String tipo, T feedback) {
                if (tipo.equals(CheckListAdapter.FEEDBACK_CHECK_SELECTED)) {
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

    protected abstract List<T> getCheckFromExisting(List<RandomCheck> rcs);

    protected abstract String getColor();

    protected abstract LiveData<List<RandomCheck>> getChecks(RandomCheckRepository repo);

    protected abstract void setTile(TextView v);
}