package devs.mrp.coolyourturkey.grupos.grupospositivos_old_deprecated.conditions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimit;
import devs.mrp.coolyourturkey.databaseroom.grouplimit.GroupLimitViewModel;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;

public class GroupLimitsFragment extends Fragment {

    private Context mContext;

    private GroupLimitViewModel mGroupLimitViewModel;

    private TextView mGroupTextView;
    private EditText mHorasEdit;
    private EditText mMinutosEdit;
    private EditText mDiasEdit;
    private Button mAddButton;
    private Switch mSoloSiCondiciones;
    private Switch mBlockSwitch;
    private RecyclerView mRecycler;
    private GroupLimitsAdapter mLimitsAdapter;

    private Integer mGroupId;
    private String mGroupName;
    private ViewModelProvider.Factory mFactory;

    private boolean viewModelStarted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication());

        if (mContext==null){mContext=getActivity();}

        mGroupLimitViewModel = new ViewModelProvider(this, mFactory).get(GroupLimitViewModel.class);

        View v = inflater.inflate(R.layout.fragment_grouplimits, container, false);

        mGroupTextView = v.findViewById(R.id.limitegroupname);
        mHorasEdit = v.findViewById(R.id.horasedit);
        mMinutosEdit = v.findViewById(R.id.minutosedit);
        mDiasEdit = v.findViewById(R.id.diasedit);
        mAddButton = v.findViewById(R.id.addbutton);
        mSoloSiCondiciones = v.findViewById(R.id.switchIfConditionsMet);
        mBlockSwitch = v.findViewById(R.id.switchBlock);
        mRecycler = v.findViewById(R.id.limitsrecycler);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    mGroupLimitViewModel.insert(getGroupLimitFromFields());
                    mHorasEdit.setText("");
                    mMinutosEdit.setText("");
                    mDiasEdit.setText("");
                    mBlockSwitch.setChecked(false);
                    mSoloSiCondiciones.setChecked(true);
                }
            }
        });

        mSoloSiCondiciones.setChecked(true);
        mLimitsAdapter = new GroupLimitsAdapter(new ArrayList<>(), mContext);
        LinearLayoutManager layoutLimits = new LinearLayoutManager(mContext);
        mRecycler.setLayoutManager(layoutLimits);
        mRecycler.setAdapter(mLimitsAdapter);

        setupViewModelOnGroupId();

        mLimitsAdapter.addFeedbackListener(new FeedbackListener<GroupLimit>() {
            @Override
            public void giveFeedback(int tipo, GroupLimit feedback, Object... args) {
                switch (tipo) {
                    case GroupLimitsAdapter.FEEDBACK_DELETE:
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.confirmacion);
                        builder.setMessage(R.string.seguro_que_deseas_borrar_este_limite);
                        builder.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGroupLimitViewModel.deleteById(feedback.getId());
                            }
                        });
                        builder.setNegativeButton(R.string.conservar, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                }
            }
        });

        updateGroupName();

        return v;
    }

    public void setGroupId(Integer id) {
        viewModelStarted = false;
        mGroupId = id;
        setupViewModelOnGroupId();
    }

    private void setupViewModelOnGroupId() {
        if (mGroupId != null && mGroupLimitViewModel != null && !viewModelStarted) {
            viewModelStarted = true;
            mGroupLimitViewModel.findByGroupId(mGroupId).observe(getViewLifecycleOwner(), new Observer<List<GroupLimit>>() {
                @Override
                public void onChanged(List<GroupLimit> groupLimits) {
                    mLimitsAdapter.updateDataSet(groupLimits);
                }
            });
        }
    }

    public void setGroupName(String name) {
        mGroupName = name;
        updateGroupName();
    }

    private void updateGroupName() {
        if (mGroupTextView != null && mGroupName != null) {
            mGroupTextView.setText(mGroupName);
        }
    }

    private boolean validateFields() {
        boolean resultado = true;

        if (isEmpty(mHorasEdit) && isEmpty(mMinutosEdit)) {
            setRed(mHorasEdit);
            setRed(mMinutosEdit);
            resultado = false;
        } else {
            if (!matchesNumber(mHorasEdit)) { setRed(mHorasEdit); resultado = false; } else { removeRed(mHorasEdit); }
            if (!matchesNumber(mMinutosEdit)) { setRed(mMinutosEdit); resultado = false; } else { removeRed(mMinutosEdit); }
        }

        if (isEmpty(mDiasEdit) || !matchesNumber(mDiasEdit)) {
            setRed(mDiasEdit);
            resultado = false;
        } else {
            removeRed(mDiasEdit);
        }

        return resultado;
    }

    private boolean matchesNumber(EditText e) {
        String s = e.getText().toString();
        if (s.matches("[0-9]*")) {
            return true;
        } return false;
    }

    private boolean isEmpty(EditText e) {
        if (e.getText().toString().equals("")) {
            return true;
        } return false;
    }

    private void setRed(EditText e) {
        e.setBackgroundColor(Color.RED);
    }

    private void removeRed(EditText e) {
        e.setBackgroundColor(Color.TRANSPARENT);
    }

    private GroupLimit getGroupLimitFromFields() {
        Integer groupId = mGroupId;
        Integer offsetDays = Integer.parseInt(mDiasEdit.getText().toString());
        Integer minutesLimit = 0;
        if (!mMinutosEdit.getText().toString().equals("")) {
            minutesLimit += Integer.parseInt(mMinutosEdit.getText().toString());
        }
        if (!mHorasEdit.getText().toString().equals("")) {
            minutesLimit += Integer.parseInt(mHorasEdit.getText().toString()) * 60;
        }
        Boolean blocking = mBlockSwitch.isChecked();
        Boolean ifCondsMet = mSoloSiCondiciones.isChecked();
        GroupLimit limit = new GroupLimit(groupId, offsetDays, minutesLimit, blocking, ifCondsMet);
        return limit;
    }

}
