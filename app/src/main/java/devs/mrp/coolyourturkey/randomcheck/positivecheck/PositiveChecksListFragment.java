package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
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

public class PositiveChecksListFragment extends Fragment implements MyObservable<PositiveCheck> {

    public static final String CALLBACK_ADD_CONDITION = "callback_add_condition";
    public static final String CALLBACK_CLICK_ELEMENT = "clicked a check from the list";

    private List<MyObserver<PositiveCheck>> observers = new ArrayList<>();

    private Context mContext;

    private RandomCheckRepository mRepo;

    private FloatingActionButton mAddButton;
    private RecyclerView mRecycler;

    @Override
    public void addObserver(MyObserver<PositiveCheck> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, PositiveCheck feedback) {
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

        LinearLayoutManager layout = new LinearLayoutManager(mContext);
        mRecycler.setLayoutManager(layout);

        CheckFactory factory = new CheckFactory();
        mRepo = RandomCheckRepository.getRepo(this.getActivity().getApplication());
        PositiveCheckListAdapter<PositiveCheck> adapter = new PositiveCheckListAdapter(mContext, PositiveCheckListAdapter.BACKGROUND_GREEN);
        mRepo.getPositiveChecks().observe(this, new Observer<List<RandomCheck>>() {
            @Override
            public void onChanged(List<RandomCheck> randomChecks) {
                List<PositiveCheck> checks = factory.positiveFrom(randomChecks);
                adapter.updateDataset(checks);
            }
        });

        adapter.addObserver(new MyObserver<PositiveCheck>() {
            @Override
            public void callback(String tipo, PositiveCheck feedback) {
                if (tipo.equals(PositiveCheckListAdapter.FEEDBACK_CHECK_SELECTED)) {
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
}
