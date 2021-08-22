package devs.mrp.coolyourturkey.condicionesnegativas;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class CondicionesNegativasFragment extends Fragment implements MyObservable<Object> {

    private List<MyObserver<Object>> observers = new ArrayList<>();
    private Context mContext;
    private ViewModelProvider.Factory factory;
    private Handler mainHandler;

    private FloatingActionButton addButton;
    private RecyclerView recycler;

    @Override
    public void addObserver(MyObserver<Object> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, Object feedback) {
        observers.stream().forEach(o -> {
            o.callback(tipo, feedback);
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());
        mainHandler = new Handler(mContext.getMainLooper());

        View v = inflater.inflate(R.layout.fragment_condiciones_negativas, container, false);

        addButton = v.findViewById(R.id.addNegCond);
        recycler = v.findViewById(R.id.recyclerNegCond);

        // TODO when deleting a positive group, delete all negative conditions depending on it

        TimeLogHandler logger = new TimeLogHandler(mContext, this.getActivity().getApplication(), this);


        return v;
    }

}
