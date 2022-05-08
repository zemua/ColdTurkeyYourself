package devs.mrp.coolyourturkey.comun;

import android.content.Context;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public abstract class FeedbackerFragment<T> extends Fragment implements Feedbacker<T> {

    private List<FeedbackListener<T>> listeners = new ArrayList<>();

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    protected Context getAttachContext() {
        return mContext;
    }

    @Override
    public void giveFeedback(int tipo, T feedback) {
        listeners.forEach(l -> l.giveFeedback(tipo, feedback));
    }

    @Override
    public void addFeedbackListener(FeedbackListener<T> listener) {
        listeners.add(listener);
    }
}
