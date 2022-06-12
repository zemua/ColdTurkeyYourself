package devs.mrp.coolyourturkey.grupos.grupospositivos_old_deprecated.conditions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.databaseroom.deprecated.conditiontogroup_old_deprecated.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.deprecated.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.deprecated.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.FTimeBlockFacade;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.ITimeBlockFacade;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class AddGroupConditionFragment extends Fragment {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";
    private static final String KEY_BUNDLE_NAME_ACTUAL = "key.bundle.name.actual";
    private static final String KEY_BUNDLE_URI = "key.bundle.uri";
    private static final String KEY_CONDITION_TYPE = "key.condition.type";
    private static final String KEY_BUNDLE_GRUPOS_POSITIVOS_LIST = "key.bundle.grupos.positivos.list";
    private static final String KEY_BUNDLE_RANDOM_CHECKS_LIST = "key.bundle.random.checks.list";
    private static final String KEY_BUNDLE_CONDITION_FOR_EDIT = "key.bundle.condition.for.edit";
    private static final String KEY_BUNDLE_IF_IS_EDIT_ACTION = "key.bundle.if.is.edit.action";

    public static final int FEEDBACK_SAVE = 0;
    public static final int FEEDBACK_DELETE_CONDITION = 1;
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
    private Spinner mRandomCheckSpinner;
    private TextView mSelectedFileTextView;
    private Button mSelectedFileButton;
    private EditText mUsedHoursEditText;
    private EditText mUsedMinutesEditText;
    private EditText mLapsedDaysEditText;
    private Button mSaveButton;
    private Button mButtonBorrar;

    private ConstraintLayout mGroupsLayout;
    private ConstraintLayout mRandomChecksLayout;
    private ConstraintLayout mFileSourceLayout;

    private List<GrupoPositivo> mGruposPositivos;
    private List<AbstractTimeBlock> mTimeBlocks;
    private ConditionToGroup mConditionForEdit;
    private boolean mIsEditAction = false;
    private boolean mViewReadyForEdit = false;
    private boolean mTargetGroupReadyForEdit = false;
    private boolean mRandomChecksReadyForEdit = false;

    // add boolean/switch whether the condition should trigger a notification when met

    public AddGroupConditionFragment(){ // needed for rotating the screen
        super();
    }

    AddGroupConditionFragment(Integer groupId, String groupName){
        super();
        mGroupId = groupId;
        mGroupName = groupName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            setGroupId(savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL));
            setGroupName(savedInstanceState.getString(KEY_BUNDLE_NAME_ACTUAL));
            if (savedInstanceState.containsKey(KEY_BUNDLE_URI)){
                mFileUri = Uri.parse(savedInstanceState.getString(KEY_BUNDLE_URI));
            }
            mConditionType = (ConditionToGroup.ConditionType) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_CONDITION_TYPE)).getData();
            mGruposPositivos = (List<GrupoPositivo>) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_GRUPOS_POSITIVOS_LIST)).getData();
            mTimeBlocks = (List<AbstractTimeBlock>) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_RANDOM_CHECKS_LIST)).getData();
            mConditionForEdit = (ConditionToGroup) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_CONDITION_FOR_EDIT)).getData();
            mIsEditAction = savedInstanceState.getBoolean(KEY_BUNDLE_IF_IS_EDIT_ACTION);
        } else {
            mGruposPositivos = new ArrayList<>();
            mTimeBlocks = new ArrayList<>();
        }
    }

    @Override
    public void onAttach(Context context) { super.onAttach(context);}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, getGroupId());
        outState.putString(KEY_BUNDLE_NAME_ACTUAL, getGroupName());
        if (mFileUri != null) {
            outState.putString(KEY_BUNDLE_URI, mFileUri.toString());
        }
        outState.putBinder(KEY_CONDITION_TYPE, new ObjectWrapperForBinder(mConditionType));
        outState.putBinder(KEY_BUNDLE_GRUPOS_POSITIVOS_LIST, new ObjectWrapperForBinder(mGruposPositivos));
        outState.putBinder(KEY_BUNDLE_RANDOM_CHECKS_LIST, new ObjectWrapperForBinder(mTimeBlocks));
        outState.putBinder(KEY_BUNDLE_CONDITION_FOR_EDIT, new ObjectWrapperForBinder(mConditionForEdit));
        outState.putBoolean(KEY_BUNDLE_IF_IS_EDIT_ACTION, mIsEditAction);
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
        mRandomCheckSpinner = v.findViewById(R.id.spinnerRandomCheck);
        mSelectedFileTextView = v.findViewById(R.id.textSelectedFile);
        mSelectedFileButton = v.findViewById(R.id.buttonSelectFile);
        mUsedHoursEditText = v.findViewById(R.id.editTextHoras);
        mUsedMinutesEditText = v.findViewById(R.id.editTextMinutos);
        mLapsedDaysEditText = v.findViewById(R.id.editTextDaysLapsed);
        mSaveButton = v.findViewById(R.id.buttonAnhadir);
        mButtonBorrar = v.findViewById(R.id.buttonBorrar);

        mGroupsLayout = v.findViewById(R.id.lineaTargetGroups);
        mRandomChecksLayout = v.findViewById(R.id.lineaTargetRandomChecks);
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
                } else if (selected.equals(getResources().getString(ConditionToGroup.ConditionType.RANDOMCHECK.getResourceId()))) {
                    showRandomCheckLayout();
                    mConditionType = ConditionToGroup.ConditionType.RANDOMCHECK;
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
        grupoRepo.findAllGrupoPositivo().observe(getViewLifecycleOwner(), new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                mGruposPositivos.clear();
                mGruposPositivos.addAll(grupoPositivos);
                ListIterator<GrupoPositivo> iterator = mGruposPositivos.listIterator();
                while (iterator.hasNext()){
                    GrupoPositivo lGrupo = iterator.next();
                    if (lGrupo.getNombre().equals(getGroupName())){
                        iterator.remove(); // remove self-group from selection
                        break;
                    }
                }
                groupsObjectList.clear();
                groupsObjectList.addAll(mGruposPositivos);
                List<String> groupNamesList = mGruposPositivos.stream().map(g -> g.getNombre()).collect(Collectors.toList());
                groupSpinerAdapter.clear();
                groupSpinerAdapter.addAll(groupNamesList);
                groupSpinerAdapter.notifyDataSetChanged();
                mTargetGroupReadyForEdit = true;
                if (mIsEditAction){setupEditExistingCondition(mConditionForEdit);}
            }
        });

        List<AbstractTimeBlock> blocksObjectList = new ArrayList<>();
        List<String> blockList = new ArrayList<>();
        ArrayAdapter<String> checkSpinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, blockList);
        checkSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRandomCheckSpinner.setAdapter(checkSpinnerAdapter);
        ITimeBlockFacade timeBlockRepo = FTimeBlockFacade.getNew(getActivity().getApplication(), getActivity());
        timeBlockRepo.getAll((tipo, blocks) -> {
            mTimeBlocks.clear();
            mTimeBlocks.addAll(blocks);
            blocksObjectList.clear();
            blocksObjectList.addAll(blocks);
            List<String>  blocksNamesList = mTimeBlocks.stream().map(b -> b.getName()).collect(Collectors.toList());
            checkSpinnerAdapter.clear();
            checkSpinnerAdapter.addAll(blocksNamesList);
            checkSpinnerAdapter.notifyDataSetChanged();
            mRandomChecksReadyForEdit = true;
            if (mIsEditAction) {setupEditExistingCondition(mConditionForEdit);}
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
                if (verifyConditionalTime() & verifyFromLastNdays() & !checkEmptyFileUri() & !checkEmptyGroup() & !checkEmptyRandomCheck()) {
                    ConditionToGroup condition = new ConditionToGroup();

                    condition.setGroupid(getGroupId());
                    condition.setType(mConditionType);
                    if (mFileUri != null) {
                        condition.setFiletarget(mFileUri.toString());
                    } else {
                        condition.setFiletarget("");
                    }
                    condition.setConditionalgroupid(mTargetGroupSpinner.getSelectedItemPosition() >= 0 ? groupsObjectList.get(mTargetGroupSpinner.getSelectedItemPosition()).getId() : -1);
                    condition.setConditionalrandomcheckid(mRandomCheckSpinner.getSelectedItemPosition() >= 0 ? blocksObjectList.get(mRandomCheckSpinner.getSelectedItemPosition()).getId() : -1);

                    Integer ltime = 0;
                    if(!mUsedHoursEditText.getText().toString().equals("")){
                        ltime += (Integer.parseInt(mUsedHoursEditText.getText().toString())*60);
                    }
                    if (!mUsedMinutesEditText.getText().toString().equals("")){
                        ltime += Integer.parseInt(mUsedMinutesEditText.getText().toString());
                    }
                    condition.setConditionalminutes(ltime);

                    condition.setFromlastndays(Integer.parseInt(mLapsedDaysEditText.getText().toString()));

                    if (mIsEditAction && mConditionForEdit != null) {
                        condition.setId(mConditionForEdit.getId());
                    }

                    mFeedbackReceiver.receiveFeedback(AddGroupConditionFragment.this, FEEDBACK_SAVE, condition);
                }
            }
        });

        if (mIsEditAction) {
            mButtonBorrar.setVisibility(View.VISIBLE);
            mButtonBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.confirmacion);
                    builder.setMessage(R.string.seguro_que_deseas_borrar_esta_condicion);
                    builder.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mFeedbackReceiver.receiveFeedback(AddGroupConditionFragment.this, FEEDBACK_DELETE_CONDITION, mConditionForEdit);
                        }
                    });
                    builder.setNegativeButton(R.string.conservar, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        mViewReadyForEdit = true;
        if (mConditionForEdit != null && mIsEditAction) {
            setupEditExistingCondition(mConditionForEdit);
        }

        return v;
    }

    private void showGroupLayout() {
        mGroupsLayout.setVisibility(View.VISIBLE);
        mFileSourceLayout.setVisibility(View.GONE);
        mRandomChecksLayout.setVisibility(View.GONE);
    }

    private void showFileLayout(){
        mGroupsLayout.setVisibility(View.GONE);
        mFileSourceLayout.setVisibility(View.VISIBLE);
        mRandomChecksLayout.setVisibility(View.GONE);
    }

    private void showRandomCheckLayout() {
        mGroupsLayout.setVisibility(View.GONE);
        mFileSourceLayout.setVisibility(View.GONE);
        mRandomChecksLayout.setVisibility(View.VISIBLE);
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
        if (mFileUri == null || !FileReader.ifHaveReadingRights(mContext, mFileUri)) {
            mSelectedFileTextView.setBackgroundColor(Color.RED);
            return true;
        }
        mSelectedFileTextView.setBackgroundColor(Color.TRANSPARENT);
        return false;
    }

    private boolean checkEmptyGroup() {
        if (mConditionType != ConditionToGroup.ConditionType.GROUP) {
            mTargetGroupSpinner.setBackgroundColor(Color.TRANSPARENT);
            return false;
        }
        int position = mTargetGroupSpinner.getSelectedItemPosition();
        if (position == -1) {
            mTargetGroupSpinner.setBackgroundColor(Color.RED);
            return true;
        }
        mTargetGroupSpinner.setBackgroundColor(Color.TRANSPARENT);
        return false;
    }

    private boolean checkEmptyRandomCheck() {
        if (mConditionType != ConditionToGroup.ConditionType.RANDOMCHECK) {
            mRandomCheckSpinner.setBackgroundColor(Color.TRANSPARENT);
            return false;
        }
        int position = mRandomCheckSpinner.getSelectedItemPosition();
        if (position == -1) {
            mRandomCheckSpinner.setBackgroundColor(Color.RED);
            return true;
        }
        mRandomCheckSpinner.setBackgroundColor(Color.TRANSPARENT);
        return false;
    }

    public void setConditionForEdit(ConditionToGroup condition) {
        mIsEditAction = true;
        mConditionForEdit = condition;
        setupEditExistingCondition(mConditionForEdit);
    }

    private void setupEditExistingCondition(ConditionToGroup condition){
        if (mViewReadyForEdit && condition != null && (condition.getType() != ConditionToGroup.ConditionType.GROUP || mTargetGroupReadyForEdit) && (condition.getType() != ConditionToGroup.ConditionType.RANDOMCHECK || mRandomChecksReadyForEdit)) {

            switch (condition.getType()) {
                case GROUP:
                    mTypeSpinner.setSelection(ConditionToGroup.ConditionType.GROUP.getPosition());
                    break;
                case RANDOMCHECK:
                    mTypeSpinner.setSelection(ConditionToGroup.ConditionType.RANDOMCHECK.getPosition());
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

            //mRandomCheckSpinner
            if (mTimeBlocks != null) {
                for (int i = 0; i<mTimeBlocks.size(); i++) {
                    if (mTimeBlocks.get(i).getId() == condition.getConditionalrandomcheckid()) {
                        mRandomCheckSpinner.setSelection(i);
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
        mButtonBorrar.setVisibility(View.GONE);
    }

}
