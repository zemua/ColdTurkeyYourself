package devs.mrp.coolyourturkey.plantillas;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FragmentFeedbacker<T> extends Fragment implements Feedbacker<T> {

    private static final String TAG = "FragmentFeedbacker";

    protected ArrayList<FeedbackListener<T>> feedbackListeners = new ArrayList<>();

    @Override
    public void giveFeedback(int tipo, T feedback){
        feedbackListeners.forEach((listener)->{
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<T> listener){
        feedbackListeners.add(listener);
        //Log.d(TAG, "feedbackListener tiene " + 1 + " entradas");
    }
}
