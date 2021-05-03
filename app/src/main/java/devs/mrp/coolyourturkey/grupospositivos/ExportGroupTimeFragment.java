package devs.mrp.coolyourturkey.grupospositivos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExport;
import devs.mrp.coolyourturkey.databaseroom.grupoexport.GrupoExportViewModel;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class ExportGroupTimeFragment extends Fragment implements Feedbacker<Object> {

    private static final String TAG = "FRAGMENT_REVIEW_GROUP";

    private static final String KEY_BUNDLE_NUM_DAYS = "key.bundle.num.days";
    private static final String KEY_BUNDLE_FILE_NAME = "key.bundle.file.name";
    private static final String KEY_BUNDLE_FILE_FIELD = "key.bundle.file.field";

    private List<FeedbackListener<Object>> feedbackListeners = new ArrayList<>();

    public static final int FEEDBACK_DONE = 0;

    private static final int REQUEST_CODE_NEW_FILE = 20;

    private Context mContext;

    private ViewModelProvider.Factory factory;
    private GrupoExportViewModel mGrupoExportViewModel;

    private Integer mGroupId;
    private String mGroupName;
    private String mFile;

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

        mFile = "";
        initializeGroupName();

        mGrupoExportViewModel = new ViewModelProvider(this, factory).get(GrupoExportViewModel.class);

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mFile = savedInstanceState.getString(KEY_BUNDLE_FILE_NAME);
            if (FileReader.ifHaveWrittingRights(mContext, Uri.parse(mFile))) {
                mFileNameTextView.setText(savedInstanceState.getString(KEY_BUNDLE_FILE_FIELD));
            }
            mDaysEditText.setText(savedInstanceState.getString(KEY_BUNDLE_NUM_DAYS));
            if (mFile.equals(mFileNameTextView.getText().toString()) && !mDaysEditText.getText().toString().equals("")) {
                mSaveButton.setVisibility(View.VISIBLE);
            }
        } else {
            mGrupoExportViewModel.findGrupoExportByGroupId(mGroupId).observe(this, new Observer<List<GrupoExport>>() {
                @Override
                public void onChanged(List<GrupoExport> grupoExports) {
                    if (grupoExports != null && grupoExports.size() > 0) {
                        mDaysEditText.setText(String.valueOf(grupoExports.get(0).getDays()));
                        if (FileReader.ifHaveWrittingRights(mContext, Uri.parse(grupoExports.get(0).getArchivo()))) {
                            mFile = grupoExports.get(0).getArchivo();
                            mFileNameTextView.setText(mFile);
                            mSaveButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }


        mSelectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nam;
                if (mGroupName != null) {
                    nam = mGroupName;
                } else {
                    nam = "";
                }
                FileReader.createTextFile(ExportGroupTimeFragment.this, REQUEST_CODE_NEW_FILE, nam);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataIsCorrect()) {
                    GrupoExport grupoExport = new GrupoExport(mGroupId, mFile, Integer.parseInt(mDaysEditText.getText().toString()));
                    mGrupoExportViewModel.insert(grupoExport);
                    giveFeedback(FEEDBACK_DONE, null);
                }
            }
        });

        return v;
    }

    private boolean dataIsCorrect() {
        boolean resultado = true;

        if (mFile.equals("") || !mFile.equals(mFileNameTextView.getText())) {
            resultado = false;
            mFileNameTextView.setBackgroundColor(Color.RED);
        } else {
            mFileNameTextView.setBackgroundColor(Color.TRANSPARENT);
        }

        String days = mDaysEditText.getText().toString();
        if (!days.matches("[0-9]+")) {
            resultado = false;
            mDaysEditText.setBackgroundColor(Color.RED);
        } else {
            mDaysEditText.setBackgroundColor(Color.TRANSPARENT);
        }

        return resultado;
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
        initializeGroupName();
    }

    private void initializeGroupName() {
        if (mGroupNameTextView != null && mGroupNameTextView.getText() != mGroupName) {
            mGroupNameTextView.setText(mGroupName);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_NEW_FILE:
                    if (resultData != null) {
                        FileReader.getFileReadPermission(mContext, resultData);
                        Uri uri = resultData.getData();
                        if (uri != null) {
                            mSaveButton.setVisibility(View.VISIBLE);
                            mFile = uri.toString();
                            mFileNameTextView.setText(mFile);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_BUNDLE_NUM_DAYS, mDaysEditText.getText().toString());
        outState.putString(KEY_BUNDLE_FILE_NAME, mFile);
        outState.putString(KEY_BUNDLE_FILE_FIELD, mFileNameTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
