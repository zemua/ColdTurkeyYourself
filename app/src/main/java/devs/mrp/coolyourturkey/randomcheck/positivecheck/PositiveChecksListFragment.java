package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;

public class PositiveChecksListFragment extends Fragment implements MyObservable<Object> {

    public static final String CALLBACK_ADD_CONDITION = "callback_add_condition";

    private List<MyObserver<Object>> observers = new ArrayList<>();

    private Context mContext;

    private FloatingActionButton mAddButton;
    private RecyclerView mRecycler;

    @Override
    public void addObserver(MyObserver<Object> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, Object feedback) {
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

        // TODO add adapter

        // TODO add repository

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCallBack(CALLBACK_ADD_CONDITION, null);
            }
        });

        return v;
    }
}
