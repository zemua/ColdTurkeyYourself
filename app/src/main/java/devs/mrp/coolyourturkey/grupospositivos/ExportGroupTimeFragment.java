package devs.mrp.coolyourturkey.grupospositivos;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExportViewModel;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class ExportGroupTimeFragment extends Fragment implements Feedbacker<Object> {

    // TODO test turning the screen works

    private static final String TAG = "FRAGMENT_REVIEW_GROUP";

    private List<FeedbackListener<Object>> feedbackListeners = new ArrayList<>();

    public static final int FEEDBACK_DONE = 0;

    private Context mContext;

    private ViewModelProvider.Factory factory;
    private GrupoExportViewModel mGrupoExportViewModel;

    private Integer mGroupId;
    private String mGroupName;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());

        if (mContext == null) {
            mContext = getActivity();
        }

        View v = inflater.inflate(R.layout.fragment_exportgrouptime, container, false);

        return v;
    }

    public void setGroupId(Integer id) {
        this.mGroupId = id;
    }

    public Integer getGroupId() {
        if (mGroupId == null) {
            return -1;
        }
        return mGroupId;
    }

    public void setGroupName(String name) {
        this.mGroupName = name;
    }

    public String getGroupName() {
        return this.mGroupName;
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
