package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class PositiveChecksFragment extends Fragment implements Feedbacker<Object> {

    private List<FeedbackListener<Object>> listeners = new ArrayList<>();

    @Override
    public void giveFeedback(int tipo, Object feedback) {
        listeners.stream().forEach(l -> {
            l.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Object> listener) {
        listeners.add(listener);
    }
}
