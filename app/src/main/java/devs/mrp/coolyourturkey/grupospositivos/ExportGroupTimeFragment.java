package devs.mrp.coolyourturkey.grupospositivos;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class ExportGroupTimeFragment extends Fragment implements Feedbacker<Object> {

    private List<FeedbackListener<Object>> feedbackListeners = new ArrayList<>();

    private Integer mGroupId;

    public void setGroupId(Integer id) {
        this.mGroupId = id;
    }

    @Override
    public void giveFeedback(int tipo, Object feedback) {
        feedbackListeners.stream().forEach(l -> {
            l.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<Object> listener) {
        feedbackListeners.add(listener);
    }
}
