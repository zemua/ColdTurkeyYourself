package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;

public class PositiveChecksListFragment extends Fragment implements MyObservable<Object> {

    private List<MyObserver<Object>> observers = new ArrayList<>();

    @Override
    public void addObserver(MyObserver<Object> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, Object feedback) {
        observers.stream().forEach(o -> o.callback(tipo, feedback));
    }
}
