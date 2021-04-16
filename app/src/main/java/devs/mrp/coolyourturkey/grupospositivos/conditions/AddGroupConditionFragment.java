package devs.mrp.coolyourturkey.grupospositivos.conditions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class AddGroupConditionFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";

    public static final int FEEDBACK_SAVE = 0;
    private static final int REQUEST_CODE_READ = 10;

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;
    private Integer mGroupId;
    private String mGroupName;
    private Uri mFileUri;
    private ConditionToGroup.ConditionType mConditionType;

    private TextView mGroupNameTextView;
    private Spinner mTypeSpinner;
    private Spinner mTargetGroupSpinner;
    private TextView mSelectedFileTextView;
    private Button mSelectedFileButton;
    private EditText mUsedHoursEditText;
    private EditText mUsedMinutesEditText;
    private EditText mLapsedDaysEditText;
    private Button mSaveButton;
    private Button mButtonBorrar; // TODO dialogo confiramción y borrar condición

    private ConstraintLayout mGroupsLayout;
    private ConstraintLayout mFileSourceLayout;

    private List<GrupoPositivo> mGruposPositivos;
    private ConditionToGroup mConditionForEdit;
    private boolean mIsEditAction = false;
    private boolean mViewReadyForEdit = false;
    private boolean mTargetGroupReadyForEdit = false;

    AddGroupConditionFragment(Integer groupId, String groupName){
        super();
        mGroupId = groupId;
        mGroupName = groupName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) { super.onAttach(context);}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, getGroupId());
        super.onSaveInstanceState(outState);
    }

    public void setGroupId(Integer groupId) {
        this.mGroupId = groupId;
    }

    public Integer getGroupId() {
        if (mGroupId == null) {
            return -1;
        }
        return mGroupId;
    }

    public void setGroupName(String groupName) {
        this.mGroupName = groupName;
    }

    public String getGroupName(){
        if (mGroupName == null){
            return "";
        }
        return mGroupName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFeedbackReceiver = (FeedbackReceiver) getActivity();
        mContext = getActivity();

        if (savedInstanceState != null && !savedInstanceState.isEmpty()){
            setGroupId(savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL));
        }

        View v = inflater.inflate(R.layout.fragment_addcondition, container, false);

        mGroupNameTextView = v.findViewById(R.id.textGroupName);
        mTypeSpinner = v.findViewById(R.id.spinnerType);
        mTargetGroupSpinner = v.findViewById(R.id.spinnerTargetGroup);
        mSelectedFileTextView = v.findViewById(R.id.textSelectedFile);
        mSelectedFileButton = v.findViewById(R.id.buttonSelectFile);
        mUsedHoursEditText = v.findViewById(R.id.editTextHoras);
        mUsedMinutesEditText = v.findViewById(R.id.editTextMinutos);
        mLapsedDaysEditText = v.findViewById(R.id.editTextDaysLapsed);
        mSaveButton = v.findViewById(R.id.buttonAnhadir);

        mGroupsLayout = v.findViewById(R.id.lineaTargetGroups);
        mFileSourceLayout = v.findViewById(R.id.lineaTargetFile);

        mGroupNameTextView.setText(mGroupName);

        List<String> typesList = ConditionToGroup.getTypesList().stream().map(s -> getResources().getString(s)).collect(Collectors.toList());
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, typesList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(typeAdapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String)parent.getItemAtPosition(position);
                if (selected.equals(getResources().getString(ConditionToGroup.ConditionType.GROUP.getResourceId()))) {
                    showGroupLayout();
                    mConditionType = ConditionToGroup.ConditionType.GROUP;
                } else if (selected.equals(getResources().getString(ConditionToGroup.ConditionType.FILE.getResourceId()))) {
                    showFileLayout();
                    mConditionType = ConditionToGroup.ConditionType.FILE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showGroupLayout();
                mConditionType = ConditionToGroup.ConditionType.GROUP;
            }
        });

        List<GrupoPositivo> groupsObjectList = new ArrayList<>(); // Reference to get the id of the selected name
        List<String> groupsList = new ArrayList<>();
        ArrayAdapter<String> groupSpinerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, groupsList);
        groupSpinerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTargetGroupSpinner.setAdapter(groupSpinerAdapter);
        GrupoPositivoRepository grupoRepo = GrupoPositivoRepository.getRepo(getActivity().getApplication());
        grupoRepo.findAllGrupoPositivo().observe(AddGroupConditionFragment.this, new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                ListIterator<GrupoPositivo> iterator = grupoPositivos.listIterator();
                while (iterator.hasNext()){
                    GrupoPositivo lGrupo = iterator.next();
                    if (lGrupo.getNombre().equals(getGroupName())){
                        iterator.remove(); // remove self-group from selection
                        break;
                    }
                }
                mGruposPositivos = grupoPositivos;
                groupsObjectList.clear();
                groupsObjectList.addAll(grupoPositivos);
                List<String> groupNamesList = grupoPositivos.stream().map(g -> g.getNombre()).collect(Collectors.toList());
                groupSpinerAdapter.clear();
                groupSpinerAdapter.addAll(groupNamesList);
                groupSpinerAdapter.notifyDataSetChanged();
                mTargetGroupReadyForEdit = true;
                if (mIsEditAction){setupEditExistingCondition(mConditionForEdit);}
            }
        });

        mSelectedFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyConditionalTime() & verifyFromLastNdays() & !checkEmptyFileUri()) {
                    ConditionToGroup condition = new ConditionToGroup();

                    condition.setGroupid(getGroupId());
                    condition.setType(mConditionType.toString());
                    if (mFileUri != null) {
                        condition.setFiletarget(mFileUri.toString());
                    } else {
                        condition.setFiletarget("");
                    }
                    condition.setConditionalgroupid(groupsObjectList.get(mTargetGroupSpinner.getSelectedItemPosition()).getId());
                    condition.setConditionalminutes((Integer.parseInt(mUsedHoursEditText.getText().toString())*60)+Integer.parseInt(mUsedMinutesEditText.getText().toString()));
                    condition.setFromlastndays(Integer.parseInt(mLapsedDaysEditText.getText().toString()));

                    if (mIsEditAction && mConditionForEdit != null) {
                        condition.setId(mConditionForEdit.getId());
                    }

                    mFeedbackReceiver.receiveFeedback(AddGroupConditionFragment.this, FEEDBACK_SAVE, condition);
                }
            }
        });

        mViewReadyForEdit = true;
        if (mConditionForEdit != null && mIsEditAction) {
            setupEditExistingCondition(mConditionForEdit);
        }

        return v;
    }

    private void showGroupLayout() {
        mGroupsLayout.setVisibility(View.VISIBLE);
        mFileSourceLayout.setVisibility(View.GONE);
    }

    private void showFileLayout(){
        mGroupsLayout.setVisibility(View.GONE);
        mFileSourceLayout.setVisibility(View.VISIBLE);
    }

    private void openFile() {
        FileReader.openTextFile(AddGroupConditionFragment.this, REQUEST_CODE_READ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_READ) {
                mFileUri = FileReader.getFileReadPermission(mContext, resultData);
                mSelectedFileTextView.setText(mFileUri.toString());
            }
        }
    }

    public ConditionToGroup.ConditionType getConditionType(){
        return mConditionType;
    }

    private boolean verifyConditionalTime(){
        String minutesContent = mUsedMinutesEditText.getText().toString();
        String hoursContent = mUsedHoursEditText.getText().toString();
        if (hoursContent.matches("[0-9]*")
                && minutesContent.matches("[0-9]*")
                && !(ifZero(hoursContent) && ifZero(minutesContent))) {
            mUsedHoursEditText.setBackgroundColor(Color.TRANSPARENT);
            mUsedMinutesEditText.setBackgroundColor(Color.TRANSPARENT);
            return true;
        } else {
            mUsedHoursEditText.setBackgroundColor(Color.RED);
            mUsedMinutesEditText.setBackgroundColor(Color.RED);
            return false;
        }
    }

    private boolean verifyFromLastNdays(){
        String nDays = mLapsedDaysEditText.getText().toString();
        if (nDays.matches("[0-9]+")){
            mLapsedDaysEditText.setBackgroundColor(Color.TRANSPARENT);
            return true;
        } else {
            mLapsedDaysEditText.setBackgroundColor(Color.RED);
            return false;
        }
    }

    private boolean ifZero(String number) {
        if (number.equals("")){
            return true;
        } if (!number.matches("[0-9]*")) {
            return true;
        } if (Integer.parseInt(number) == 0) {
            return true;
        }
        return false;
    }

    private boolean checkEmptyFileUri(){
        if (mConditionType != ConditionToGroup.ConditionType.FILE) {
            mSelectedFileTextView.setBackgroundColor(Color.TRANSPARENT);
            return false;
        }
        if (mFileUri == null) {
            mSelectedFileTextView.setBackgroundColor(Color.RED);
            return true;
        }
        mSelectedFileTextView.setBackgroundColor(Color.TRANSPARENT);
        return false;
    }

    public void setConditionForEdit(ConditionToGroup condition) {
        mIsEditAction = true;
        mConditionForEdit = condition;
        setupEditExistingCondition(mConditionForEdit);
    }

    private void setupEditExistingCondition(ConditionToGroup condition){
        if (mViewReadyForEdit && condition != null && (ConditionToGroup.ConditionType.valueOf(condition.getType()) != ConditionToGroup.ConditionType.GROUP || mTargetGroupReadyForEdit)) {

            switch (ConditionToGroup.ConditionType.valueOf(condition.getType())) {
                case GROUP:
                    mTypeSpinner.setSelection(ConditionToGroup.ConditionType.GROUP.getPosition());
                    break;
                case FILE:
                    mTypeSpinner.setSelection(ConditionToGroup.ConditionType.FILE.getPosition());
                    break;
            }

            //mTargetGroupSpinner
            if (mGruposPositivos != null) {
                for (int i = 0; i < mGruposPositivos.size(); i++) {
                    if (mGruposPositivos.get(i).getId() == condition.getConditionalgroupid()) {
                        mTargetGroupSpinner.setSelection(i);
                    }
                }
            }

            mSelectedFileTextView.setText(condition.getFiletarget());
            if (!condition.getFiletarget().equals("")) {
                mFileUri = Uri.parse(condition.getFiletarget());
            }

            mUsedHoursEditText.setText(String.valueOf(condition.getConditionalminutes() / 60));

            mUsedMinutesEditText.setText(String.valueOf(condition.getConditionalminutes() % 60));

            mLapsedDaysEditText.setText(String.valueOf(condition.getFromlastndays()));
        }
    }

    public void setupClearEditMode(){
        mIsEditAction = false;
        mConditionForEdit = null;
    }

}
