package devs.mrp.coolyourturkey.randomcheck.timeblocks.review;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public class ATimeBlocksFragment<T extends AbstractTimeBlock> extends Fragment implements MyObservable<T> {

    protected List<MyObserver<T>> observers = new ArrayList<>();

    public static final String FEEDBACK_SAVE_NEW = "nuevo";
    public static final String FEEDBACK_SAVE_EXISTING = "existente";
    public static final String FEEDBACK_DELETE_THIS = "delete";

    protected Context mContext;
    protected T mTimeBlock;
    protected String mCurrent;

    // TODO add fields of view

    @Override
    public void addObserver(MyObserver<T> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, T feedback) {
        observers.forEach(o -> o.callback(tipo, feedback));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }
}
