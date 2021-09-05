package devs.mrp.coolyourturkey.checkperformer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;

public class PriceConfirmationFragment extends Fragment implements MyObservable<Boolean> {

    private List<MyObserver<Boolean>> observers = new ArrayList<>();

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_price_confirmation, container, false);

        TextView txt = mView.findViewById(R.id.textViewRecompensa);

        Button btn = mView.findViewById(R.id.buttonOk);
        btn.setOnClickListener(view -> doCallBack("", true));

        return mView;
    }

    @Override
    public void addObserver(MyObserver<Boolean> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, Boolean feedback) {
        observers.forEach(o -> o.callback(tipo, feedback));
    }
}
