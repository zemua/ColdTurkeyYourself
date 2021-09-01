package devs.mrp.coolyourturkey.checkperformer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservableNegative;
import devs.mrp.coolyourturkey.comun.MyObservablePositive;
import devs.mrp.coolyourturkey.comun.MyObserver;

public class CheckPerformerFragment extends Fragment implements MyObservableNegative<Boolean>, MyObservablePositive<Boolean> {

    private List<MyObserver<Boolean>> negativeObservers = new ArrayList<>();
    private List<MyObserver<Boolean>> positiveObservers = new ArrayList<>();

    private View mView;

    private String mQuestion;
    private boolean mNoWaits;
    private boolean mYesWaits;
    private boolean mFeedback;

    public CheckPerformerFragment(String question, boolean noWaits, boolean yesWaits, Boolean feedback) {
        mQuestion = question;
        mNoWaits = noWaits;
        mYesWaits = yesWaits;
        mFeedback = feedback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_random_control, container, false);

        // TODO

        return mView;
    }

    @Override
    public void addNegativeObserver(MyObserver<Boolean> observer) {
        negativeObservers.add(observer);
    }

    @Override
    public void doNegativeCallback(Boolean feedback) {
        negativeObservers.forEach(o -> o.callback("no", mFeedback));
    }

    @Override
    public void addPositiveObserver(MyObserver<Boolean> observer) {
        positiveObservers.add(observer);
    }

    @Override
    public void doPositiveCallback(Boolean feedback) {
        positiveObservers.forEach(o -> o.callback("yes", mFeedback));
    }
}
