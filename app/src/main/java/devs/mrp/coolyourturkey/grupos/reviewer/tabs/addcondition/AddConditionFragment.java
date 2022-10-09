package devs.mrp.coolyourturkey.grupos.reviewer.tabs.addcondition;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.FeedbackerFragment;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.GrupoRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.grupos.reviewer.tabs.ConditionBuildHelper;

public class AddConditionFragment extends FeedbackerFragment<GrupoCondition> {

    private static final String KEY_BUNDLE_ID_ACTUAL = "key.bundle.id.actual";
    private static final String KEY_BUNDLE_NAME_ACTUAL = "key.bundle.name.actual";
    private static final String KEY_BUNDLE_GRUPOS_LIST = "key.bundle.grupos.positivos.list";
    private static final String KEY_BUNDLE_CONDITION_FOR_EDIT = "key.bundle.condition.for.edit";
    private static final String KEY_BUNDLE_IF_IS_EDIT_ACTION = "key.bundle.if.is.edit.action";

    private int mGroupId;
    private String mGroupName;
    private List<Grupo> mGrupos;
    private GrupoCondition mGrupoCondition;
    private boolean mIsEdit = false;
    private ConditionBuildHelper mConditionHelper;

    private TextView mGroupNameTextView;
    private Spinner mTargetGroupSpinner;
    private EditText mUsedHoursEditText;
    private EditText mUsedMinutesEditText;
    private EditText mLapsedDaysEditText;
    private Button mSaveButton;
    private Button mButtonBorrar;

    public AddConditionFragment() {
        super();
    }

    public AddConditionFragment(int groupId, String groupName) {
        this(groupId, groupName, new GrupoCondition(), false);
    }

    public AddConditionFragment(int groupId, String groupName, GrupoCondition condition, boolean isEdit) {
        super();
        this.mGroupId = groupId;
        this.mGroupName = groupName;
        this.mGrupoCondition = condition;
        this.mIsEdit = isEdit;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL, -1);
            mGroupName = savedInstanceState.getString(KEY_BUNDLE_NAME_ACTUAL, "");
            mGrupos = (List<Grupo>) Optional.ofNullable(((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_GRUPOS_LIST))).map(ObjectWrapperForBinder::getData).orElse(new ArrayList<>());
            mGrupoCondition = (GrupoCondition) Optional.ofNullable(((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_CONDITION_FOR_EDIT))).map(ObjectWrapperForBinder::getData).orElse(new GrupoCondition());
            mIsEdit = savedInstanceState.getBoolean(KEY_BUNDLE_IF_IS_EDIT_ACTION, false);
        } else {
            mGrupos = new ArrayList<>();
        }
        mConditionHelper = new ConditionBuildHelper();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_BUNDLE_ID_ACTUAL, mGroupId);
        outState.putString(KEY_BUNDLE_NAME_ACTUAL, mGroupName);
        outState.putBinder(KEY_BUNDLE_GRUPOS_LIST, new ObjectWrapperForBinder(mGrupos));
        outState.putBinder(KEY_BUNDLE_CONDITION_FOR_EDIT, new ObjectWrapperForBinder(mGrupoCondition));
        outState.putBoolean(KEY_BUNDLE_IF_IS_EDIT_ACTION, mIsEdit);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            mGroupId = savedInstanceState.getInt(KEY_BUNDLE_ID_ACTUAL);
        }

        View v = inflater.inflate(R.layout.fragment_addcondition_group, container, false);

        mGroupNameTextView = v.findViewById(R.id.textGroupName);
        mTargetGroupSpinner = v.findViewById(R.id.spinnerTargetGroup);
        mUsedHoursEditText = v.findViewById(R.id.editTextHoras);
        mUsedMinutesEditText = v.findViewById(R.id.editTextMinutos);
        mLapsedDaysEditText = v.findViewById(R.id.editTextDaysLapsed);
        mSaveButton = v.findViewById(R.id.buttonAnhadir);
        mButtonBorrar = v.findViewById(R.id.buttonBorrar);

        mGroupNameTextView.setText(mGroupName);

        if (mGrupos == null) {
            mGrupos = new ArrayList<>();
        }
        List<String> groupsList = new ArrayList<>();
        ArrayAdapter<String> groupSpinnerAdapter = new ArrayAdapter<>(getAttachContext(), android.R.layout.simple_spinner_item, groupsList);
        groupSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTargetGroupSpinner.setAdapter(groupSpinnerAdapter);
        GrupoRepository grupoRepo = GrupoRepository.getRepo(getActivity().getApplication());
        grupoRepo.findGruposPositivos().observe(getViewLifecycleOwner(), grupos -> {
            mGrupos.clear();
            mGrupos.addAll(grupos);
            ListIterator<Grupo> iterator = mGrupos.listIterator();
            while (iterator.hasNext()) {
                Grupo g = iterator.next();
                if (g.getId().equals(mGroupId)) {
                    iterator.remove();
                    break;
                }
            }
            List<String> groupNames = mGrupos.stream().map(Grupo::getNombre).collect(Collectors.toList());
            groupSpinnerAdapter.clear();
            groupSpinnerAdapter.addAll(groupNames);
            groupSpinnerAdapter.notifyDataSetChanged();
            if (mIsEdit) {
                mConditionHelper.setupEditExistingCondition(mGrupoCondition, mTargetGroupSpinner, mGrupos, mUsedHoursEditText, mUsedMinutesEditText, mLapsedDaysEditText);
            }
        });

        mSaveButton.setOnClickListener(view -> {
            if (!mConditionHelper.verifyData(mUsedMinutesEditText, mUsedHoursEditText, mLapsedDaysEditText, mTargetGroupSpinner)) {
                return;
            }
            GrupoCondition condition = new GrupoCondition();
            condition.setGroupid(mGroupId);
            condition.setConditionalgroupid(mTargetGroupSpinner.getSelectedItemPosition() >= 0 ? mGrupos.get(mTargetGroupSpinner.getSelectedItemPosition()).getId() : -1);
            condition.setConditionalminutes(mConditionHelper.getConditionalMinutes(mUsedHoursEditText, mUsedMinutesEditText));
            condition.setFromlastndays(Integer.parseInt(mLapsedDaysEditText.getText().toString()));
            if (mIsEdit && mGrupoCondition != null) {
                condition.setId(mGrupoCondition.getId());
            }

            giveFeedback(ConditionActionConstants.ACTION_SAVE, condition);
        });

        if (mIsEdit) {
            mButtonBorrar.setVisibility(View.VISIBLE);
            mButtonBorrar.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getAttachContext());
                builder.setTitle(R.string.confirmacion);
                builder.setMessage(R.string.seguro_que_deseas_borrar_esta_condicion);
                builder.setPositiveButton(R.string.borrar, (dialog, which) -> {
                    giveFeedback(ConditionActionConstants.ACTION_DELETE, mGrupoCondition);
                });
                builder.setNegativeButton(R.string.conservar, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }

        return v;
    }
}
