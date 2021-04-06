package devs.mrp.coolyourturkey.grupospositivos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class ReviewGroupFragment extends Fragment {

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;

    private TextView textApps;
    private RecyclerView recyclerApps;
    private TextView textConditions;
    private RecyclerView recyclerConditions;
    private Button deleteButton;
    private Button exportButton;

    private ReviewGroupAppsAdapter mAppsAdapter;
    private ReviewGroupsConditionsAdapter mConditionsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        mFeedbackReceiver = (FeedbackReceiver) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reviewgroup, container, false);

        textApps = v.findViewById(R.id.textapps);
        recyclerApps = v.findViewById(R.id.recyclerAppsGrupo);
        textConditions = v.findViewById(R.id.textCondiciones);
        recyclerConditions = v.findViewById(R.id.recyclerCondiciones);
        deleteButton = v.findViewById(R.id.buttonDelete);
        exportButton = v.findViewById(R.id.buttonExp);

        return v;
    }
}
