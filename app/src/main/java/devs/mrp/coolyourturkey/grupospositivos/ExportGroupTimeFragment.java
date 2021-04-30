package devs.mrp.coolyourturkey.grupospositivos;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExport;
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

    private TextView mGroupNameTextView;
    private EditText mDaysEditText;
    private TextView mFileNameTextView;
    private Button mSelectFileButton;
    private Button mSaveButton;

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

        mGroupNameTextView = v.findViewById(R.id.groupName);
        mDaysEditText = v.findViewById(R.id.editTextDays);
        mFileNameTextView = v.findViewById(R.id.textFileName);
        mSelectFileButton = v.findViewById(R.id.buttonFile);
        mSaveButton = v.findViewById(R.id.buttonSave);

        mGrupoExportViewModel = new ViewModelProvider(this, factory).get(GrupoExportViewModel.class);
        mGrupoExportViewModel.findGrupoExportByGroupId(mGroupId).observe(this, new Observer<List<GrupoExport>>() {
            @Override
            public void onChanged(List<GrupoExport> grupoExports) {
                if (grupoExports != null && grupoExports.size() > 0) {
                    // TODO
                    //mDaysEditText.setText(grupoExports.get(0).);
                }
            }
        });

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
        mGroupNameTextView.setText(name);
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
