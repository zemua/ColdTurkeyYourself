package devs.mrp.coolyourturkey.randomcheck.timeblocks.export;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export.TimeBlockExport;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export.TimeBlockExportViewModel;

public class ExportBlockFragment extends Fragment implements MyObservable<Object> {

    public static final String FEEDBACK_DONE = "done.with";

    private static final String KEY_BUNDLE_NUM_DAYS = "key.bundle.num.days";
    private static final String KEY_BUNDLE_FILE_NAME = "key.bundle.file.name";
    private static final String KEY_BUNDLE_FILE_FIELD = "key.bundle.file.field";

    private static final int REQUEST_CODE_NEW_FILE = 20;

    private List<MyObserver<Object>> observers = new ArrayList<>();

    private Context mContext;

    private ViewModelProvider.Factory factory;
    private TimeBlockExportViewModel mBlockExportViewModel;

    private Integer mBlockId;
    private String mBlockName;
    private String mFile;

    private TextView mBlockNameTextView;
    private EditText mDaysEditText;
    private TextView mFileNameTextView;
    private Button mSelectFileButton;
    private Button mSaveButton;

    public ExportBlockFragment(Integer blockId, String blockName) {
        this.mBlockId = blockId;
        this.mBlockName = blockName;
    }

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

        mBlockNameTextView = v.findViewById(R.id.groupName);
        mDaysEditText = v.findViewById(R.id.editTextDays);
        mFileNameTextView = v.findViewById(R.id.textFileName);
        mSelectFileButton = v.findViewById(R.id.buttonFile);
        mSaveButton = v.findViewById(R.id.buttonSave);

        mFile = "";
        initializeGroupName();

        mBlockExportViewModel = new ViewModelProvider(this, factory).get(TimeBlockExportViewModel.class);

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
            mBlockExportViewModel.findTimeBlockExportByBlockId(mBlockId).observe(this, new Observer<List<TimeBlockExport>>() {
                @Override
                public void onChanged(List<TimeBlockExport> grupoExports) {
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
                if (mBlockName != null) {
                    nam = mBlockName;
                } else {
                    nam = "";
                }
                FileReader.createTextFile(ExportBlockFragment.this, REQUEST_CODE_NEW_FILE, nam);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataIsCorrect()) {
                    TimeBlockExport blockExport = new TimeBlockExport(mBlockId, mFile, Integer.parseInt(mDaysEditText.getText().toString()));
                    mBlockExportViewModel.insert(blockExport);
                    doCallBack(FEEDBACK_DONE, null);
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

    public void setBlockId(Integer id) {
        this.mBlockId = id;
    }

    public Integer getBlockId() {
        if (mBlockId == null) {
            return -1;
        }
        return mBlockId;
    }

    public void setBlockName(String name) {
        this.mBlockName = name;
        initializeGroupName();
    }

    private void initializeGroupName() {
        if (mBlockNameTextView != null && mFileNameTextView.getText() != mBlockName) {
            mBlockNameTextView.setText(mBlockName);
        }
    }

    public String getGroupName() {
        return this.mBlockName;
    }

    @Override
    public void addObserver(MyObserver<Object> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, Object feedback) {
        observers.forEach(o -> o.callback(tipo, feedback));
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
