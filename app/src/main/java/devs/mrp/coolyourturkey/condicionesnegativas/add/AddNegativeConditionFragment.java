package devs.mrp.coolyourturkey.condicionesnegativas.add;

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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.comun.MyBeanFactory;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup_old_deprecated.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivo;
import devs.mrp.coolyourturkey.databaseroom.grupopositivo.GrupoPositivoRepository;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.FTimeBlockFacade;
import devs.mrp.coolyourturkey.dtos.timeblock.facade.ITimeBlockFacade;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class AddNegativeConditionFragment extends Fragment implements Feedbacker<ConditionNegativeToGroup> {

    private List<FeedbackListener<ConditionNegativeToGroup>> listeners = new ArrayList<>();

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";
    private static final String KEY_BUNDLE_NAME_ACTUAL = "key.bundle.name.actual";
    private static final String KEY_BUNDLE_URI = "key.bundle.uri";
    private static final String KEY_CONDITION_TYPE = "key.condition.type";
    private static final String KEY_BUNDLE_GRUPOS_POSITIVOS_LIST = "key.bundle.grupos.positivos.list";
    private static final String KEY_BUNDLE_TIME_BLOCKS_LIST = "key.bundle.time.blocks.list";
    private static final String KEY_BUNDLE_CONDITION_FOR_EDIT = "key.bundle.condition.for.edit";
    private static final String KEY_BUNDLE_IF_IS_EDIT_ACTION = "key.bundle.if.is.edit.action";

    public static final int FEEDBACK_SAVE = 0;
    public static final int FEEDBACK_DELETE_CONDITION = 1;
    private static final int REQUEST_CODE_READ = 10;

    private boolean mIsEditAction = false;
    private ConditionNegativeToGroup mConditionForEdit;
    private boolean mViewReadyForEdit = false; // view items already initialized?
    private boolean mTargetGroupReadyForEdit = false; // groups already loaded from the db?
    private boolean mBlocksReadyForEdit = false;
    private Uri mFileUri;
    private Integer mConditionId;
    private ConditionNegativeToGroup.ConditionType mConditionType;
    private List<GrupoPositivo> mGruposPositivos;
    private List<AbstractTimeBlock> mTimeBlocks;
    private Context mContext;

    private Spinner mTypeSpinner;
    private Spinner mTargetGroupSpinner;
    private Spinner mTimeBlockSpinner;
    private TextView mSelectedFileTextView;
    private TextView mUsedHoursEditText;
    private TextView mUsedMinutesEditText;
    private TextView mLapsedDaysEditText;
    private Button mSelectedFileButton;
    private Button mSaveButton;
    private Button mButtonBorrar;

    private ConstraintLayout mGroupsLayout;
    private ConstraintLayout mFileSourceLayout;
    private ConstraintLayout mRandomCheckLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mConditionId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL);
            if (savedInstanceState.containsKey(KEY_BUNDLE_URI)) {
                mFileUri = Uri.parse(savedInstanceState.getString(KEY_BUNDLE_URI));
            }
            mConditionType = (ConditionNegativeToGroup.ConditionType) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_CONDITION_TYPE)).getData();
            mGruposPositivos = (List<GrupoPositivo>) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_GRUPOS_POSITIVOS_LIST)).getData();
            mTimeBlocks = (List<AbstractTimeBlock>) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_TIME_BLOCKS_LIST)).getData();
            mConditionForEdit = (ConditionNegativeToGroup) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_CONDITION_FOR_EDIT)).getData();
            mIsEditAction = savedInstanceState.getBoolean(KEY_BUNDLE_IF_IS_EDIT_ACTION);
        } else {
            mGruposPositivos = new ArrayList<>();
            mTimeBlocks = new ArrayList<>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, getConditionId());
        if (mFileUri != null) {
            outState.putString(KEY_BUNDLE_URI, mFileUri.toString());
        }
        outState.putBinder(KEY_CONDITION_TYPE, new ObjectWrapperForBinder(mConditionType));
        outState.putBinder(KEY_BUNDLE_GRUPOS_POSITIVOS_LIST, new ObjectWrapperForBinder(mGruposPositivos));
        outState.putBinder(KEY_BUNDLE_TIME_BLOCKS_LIST, new ObjectWrapperForBinder(mTimeBlocks));
        outState.putBinder(KEY_BUNDLE_CONDITION_FOR_EDIT, new ObjectWrapperForBinder(mConditionForEdit));
        outState.putBoolean(KEY_BUNDLE_IF_IS_EDIT_ACTION, mIsEditAction);
        super.onSaveInstanceState(outState);
    }

    public Integer getConditionId() {
        if (mConditionId == null) {
            return -1;
        }
        return mConditionId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();

        if (savedInstanceState != null && !savedInstanceState.isEmpty()){
            mConditionId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL);
        }

        View v = inflater.inflate(R.layout.fragment_addcondition, container, false);

        mTypeSpinner = v.findViewById(R.id.spinnerType);
        mTargetGroupSpinner = v.findViewById(R.id.spinnerTargetGroup);
        mTimeBlockSpinner = v.findViewById(R.id.spinnerRandomCheck);
        mSelectedFileTextView = v.findViewById(R.id.textSelectedFile);
        mSelectedFileButton = v.findViewById(R.id.buttonSelectFile);
        mUsedHoursEditText = v.findViewById(R.id.editTextHoras);
        mUsedMinutesEditText = v.findViewById(R.id.editTextMinutos);
        mLapsedDaysEditText = v.findViewById(R.id.editTextDaysLapsed);
        mSaveButton = v.findViewById(R.id.buttonAnhadir);
        mButtonBorrar = v.findViewById(R.id.buttonBorrar);

        mGroupsLayout = v.findViewById(R.id.lineaTargetGroups);
        mRandomCheckLayout = v.findViewById(R.id.lineaTargetRandomChecks);
        mFileSourceLayout = v.findViewById(R.id.lineaTargetFile);

        ((TextView)v.findViewById(R.id.textGroupName)).setText(R.string.apps_malas);

        List<String> typesList = ConditionToGroup.getTypesList().stream().map(s -> getResources().getString(s)).collect(Collectors.toList());
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, typesList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(typeAdapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String)parent.getItemAtPosition(position);
                if (selected.equals(getResources().getString(ConditionNegativeToGroup.ConditionType.GROUP.getResourceId()))) {
                    showGroupLayout();
                    mConditionType = ConditionNegativeToGroup.ConditionType.GROUP;
                } else if (selected.equals(getResources().getString(ConditionNegativeToGroup.ConditionType.RANDOMCHECK.getResourceId()))) {
                    showRandomCheckLayout();
                    mConditionType = ConditionNegativeToGroup.ConditionType.RANDOMCHECK;
                } else if (selected.equals(getResources().getString(ConditionNegativeToGroup.ConditionType.FILE.getResourceId()))) {
                    showFileLayout();
                    mConditionType = ConditionNegativeToGroup.ConditionType.FILE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showGroupLayout();
                mConditionType = ConditionNegativeToGroup.ConditionType.GROUP;
            }
        });

        List<GrupoPositivo> groupsObjectList = new ArrayList<>(); // Reference to get the id of the selected name
        List<String> groupsList = new ArrayList<>();
        ArrayAdapter<String> groupSpinerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, groupsList);
        groupSpinerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTargetGroupSpinner.setAdapter(groupSpinerAdapter);
        GrupoPositivoRepository grupoRepo = GrupoPositivoRepository.getRepo(getActivity().getApplication());
        grupoRepo.findAllGrupoPositivo().observe(AddNegativeConditionFragment.this.getViewLifecycleOwner(), new Observer<List<GrupoPositivo>>() {
            @Override
            public void onChanged(List<GrupoPositivo> grupoPositivos) {
                mGruposPositivos.clear();
                mGruposPositivos.addAll(grupoPositivos);
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
        List<String> blocksList = new ArrayList<>();
        ArrayAdapter<String> blockSpinerAdaper = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, blocksList);
        blockSpinerAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeBlockSpinner.setAdapter(blockSpinerAdaper);
        ITimeBlockFacade blockFacade = FTimeBlockFacade.getNew(getActivity().getApplication(), getActivity());
        blockFacade.getAll((tipo, feedback) -> {
            mTimeBlocks.clear();
            mTimeBlocks.addAll(feedback);
            blocksObjectList.clear();
            blocksObjectList.addAll(mTimeBlocks);
            List<String> blockNamesList = mTimeBlocks.stream().map(b -> b.getName()).collect(Collectors.toList());
            blockSpinerAdaper.clear();
            blockSpinerAdaper.addAll(blockNamesList);
            blockSpinerAdaper.notifyDataSetChanged();
            mBlocksReadyForEdit = true;
            if (mIsEditAction){setupEditExistingCondition(mConditionForEdit);}
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
                if (verifyConditionalTime() & verifyFromLastNdays() & !checkEmptyFileUri() & !checkEmptyGroup() & !checkEmptyBlock()) {
                    ConditionNegativeToGroup condition = MyBeanFactory.getNewNegativeCondition();

                    condition.setId(mConditionId);
                    condition.setType(mConditionType);

                    condition.setFiletarget(mFileUri != null ? mFileUri.toString() : "");
                    condition.setConditionalgroupid(groupsObjectList.size() > 0 ? groupsObjectList.get(mTargetGroupSpinner.getSelectedItemPosition()).getId() : -1);
                    condition.setConditionalblockid(blocksObjectList.size() > 0 ? blocksObjectList.get(mTimeBlockSpinner.getSelectedItemPosition()).getId() : -1);

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

                    giveFeedback(FEEDBACK_SAVE, condition);
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
                            giveFeedback(FEEDBACK_DELETE_CONDITION, mConditionForEdit);
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
        mRandomCheckLayout.setVisibility(View.GONE);
        mFileSourceLayout.setVisibility(View.GONE);
    }

    private void showRandomCheckLayout() {
        mGroupsLayout.setVisibility(View.GONE);
        mRandomCheckLayout.setVisibility(View.VISIBLE);
        mFileSourceLayout.setVisibility(View.GONE);
    }

    private void showFileLayout(){
        mGroupsLayout.setVisibility(View.GONE);
        mRandomCheckLayout.setVisibility(View.GONE);
        mFileSourceLayout.setVisibility(View.VISIBLE);
    }

    private void openFile() {
        FileReader.openTextFile(AddNegativeConditionFragment.this, REQUEST_CODE_READ);
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

    public void setConditionForEdit(ConditionNegativeToGroup condition) {
        mIsEditAction = true;
        mConditionForEdit = condition;
        setupEditExistingCondition(condition);
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
        if (mConditionType != ConditionNegativeToGroup.ConditionType.FILE) {
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
        if (mConditionType != ConditionNegativeToGroup.ConditionType.GROUP) {
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

    private boolean checkEmptyBlock() {
        if (mConditionType != ConditionNegativeToGroup.ConditionType.RANDOMCHECK) {
            mTimeBlockSpinner.setBackgroundColor(Color.TRANSPARENT);
            return false;
        }
        int position = mTimeBlockSpinner.getSelectedItemPosition();
        if (position == -1) {
            mTargetGroupSpinner.setBackgroundColor(Color.RED);
            return true;
        }
        mTimeBlockSpinner.setBackgroundColor(Color.TRANSPARENT);
        return false;
    }

    private void setupEditExistingCondition(ConditionNegativeToGroup condition) {
        if (mViewReadyForEdit && condition != null && (condition.getType() != ConditionNegativeToGroup.ConditionType.GROUP || mTargetGroupReadyForEdit) && (condition.getType() != ConditionNegativeToGroup.ConditionType.RANDOMCHECK || mBlocksReadyForEdit)) {

            switch (condition.getType()) {
                case GROUP:
                    mTypeSpinner.setSelection(ConditionNegativeToGroup.ConditionType.GROUP.getPosition());
                    break;
                case RANDOMCHECK:
                    mTypeSpinner.setSelection(ConditionNegativeToGroup.ConditionType.RANDOMCHECK.getPosition());
                    break;
                case FILE:
                    mTypeSpinner.setSelection(ConditionNegativeToGroup.ConditionType.FILE.getPosition());
                    break;
            }

            // groups spinner
            if (mGruposPositivos != null) {
                for (int i=0; i<mGruposPositivos.size(); i++) {
                    if (mGruposPositivos.get(i).getId() == condition.getConditionalgroupid()) {
                        mTargetGroupSpinner.setSelection(i);
                    }
                }
            }

            // blocks spinner
            if (mTimeBlocks != null) {
                for (int i =0; i<mTimeBlocks.size(); i++) {
                    if (mTimeBlocks.get(i).getId() == condition.getConditionalblockid()) {
                        mTimeBlockSpinner.setSelection(i);
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

    @Override
    public void giveFeedback(int tipo, ConditionNegativeToGroup feedback) {
        listeners.stream().forEach(l -> {
            l.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<ConditionNegativeToGroup> listener) {
        listeners.add(listener);
    }
}
