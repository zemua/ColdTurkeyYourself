package devs.mrp.coolyourturkey.randomcheck.timeblocks.review;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.ASelectablesFacade;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.FDbChecksAsSelectable;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.FSelectablesFacade;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.INegativeAsSelectable;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.IPositiveAsSelectable;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.SelectableFacade;
import devs.mrp.coolyourturkey.dtos.randomcheck.ANegativeCheckSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.APositiveCheckSelectable;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.FTimeBlockWithSelectableChecks;
import devs.mrp.coolyourturkey.dtos.timeblock.TimeBlockFactory;

public class TimeBlocksFragment extends Fragment implements MyObservable<AbstractTimeBlock> {

    protected List<MyObserver<AbstractTimeBlock>> observers = new ArrayList<>();

    public static final String FEEDBACK_SAVE_NEW = "nuevo";
    public static final String FEEDBACK_SAVE_EXISTING = "existente";
    public static final String FEEDBACK_DELETE_THIS = "delete";

    protected Context mContext;
    protected AbstractTimeBlock mTimeBlock;
    protected String mCurrentFeedback;

    protected View mView;
    protected EditText mName;
    protected Button mPreHoraButton;
    protected Button mPostHoraButton;
    protected Button mControlMin;
    protected Button mControlMax;
    protected CheckBox mLunesCheck;
    protected CheckBox mMartesCheck;
    protected CheckBox mMiercolesCheck;
    protected CheckBox mJuevesCheck;
    protected CheckBox mViernesCheck;
    protected CheckBox mSabadoCheck;
    protected CheckBox mDomingoCheck;
    protected RecyclerView mNegativeRecycler;
    protected RecyclerView mPositiveRecycler;
    protected Button mDeleteBlock;
    protected Button mSaveBlock;

    private SelectablesAdapter<APositiveCheckSelectable> mPositiveAdapter;
    private SelectablesAdapter<ANegativeCheckSelectable> mNegativeAdapter;

    protected int initH = 0;
    protected int initM = 0;
    protected int finH = 0;
    protected int finM = 0;

    protected int fromH = 0;
    protected int fromM = 0;
    protected int toH = 0;
    protected int toM = 0;

    public TimeBlocksFragment() {
        super();
    }

    public TimeBlocksFragment(AbstractTimeBlock timeBlock) {
        super();
        mTimeBlock = timeBlock;
    }

    @Override
    public void addObserver(MyObserver<AbstractTimeBlock> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, AbstractTimeBlock feedback) {
        observers.forEach(o -> o.callback(tipo, feedback));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_time_block, container, false);

        mName = mView.findViewById(R.id.editTextNameBlock);
        mPreHoraButton = mView.findViewById(R.id.buttonPreHora);
        mPostHoraButton = mView.findViewById(R.id.buttonPostHora);
        mControlMin = mView.findViewById(R.id.buttonControlMin);
        mControlMax = mView.findViewById(R.id.buttonControlMax);
        mLunesCheck = mView.findViewById(R.id.checkBoxLunes);
        mMartesCheck = mView.findViewById(R.id.checkBoxMartes);
        mMiercolesCheck = mView.findViewById(R.id.checkBoxMiercoles);
        mJuevesCheck = mView.findViewById(R.id.checkBoxJueves);
        mViernesCheck = mView.findViewById(R.id.checkBoxViernes);
        mSabadoCheck = mView.findViewById(R.id.checkBoxSabado);
        mDomingoCheck = mView.findViewById(R.id.checkBoxDomingo);
        mNegativeRecycler = mView.findViewById(R.id.recyclerControlesNegativos);
        mPositiveRecycler = mView.findViewById(R.id.recyclerControlesPositivos);
        mDeleteBlock = mView.findViewById(R.id.buttonDeleteBlock);
        mSaveBlock = mView.findViewById(R.id.buttonSaveBlock);

        LinearLayoutManager layout1 = new LinearLayoutManager(mContext);
        mNegativeRecycler.setLayoutManager(layout1);

        LinearLayoutManager layout2 = new LinearLayoutManager(mContext);
        mPositiveRecycler.setLayoutManager(layout2);

        if (mTimeBlock == null) {
            mCurrentFeedback = FEEDBACK_SAVE_NEW;
            mTimeBlock = new TimeBlockFactory().getNew();
            mDeleteBlock.setVisibility(View.GONE);
        } else {
            mCurrentFeedback = FEEDBACK_SAVE_EXISTING;
            mDeleteBlock.setVisibility(View.VISIBLE);
            fillFieldsWithExistingData(mTimeBlock);
        }

        mSaveBlock.setOnClickListener(v -> guardar(v));

        mDeleteBlock.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.confirmacion);
            builder.setMessage(R.string.seguro_que_deseas_borrar_este_bloque_de_tiempo);
            builder.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doCallBack(FEEDBACK_DELETE_THIS, mTimeBlock);
                }
            });
            builder.setNegativeButton(R.string.conservar, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        mPositiveAdapter = new SelectablesAdapter<>();
        mNegativeAdapter = new SelectablesAdapter<>();

        if (mCurrentFeedback.equals(FEEDBACK_SAVE_EXISTING)){
            ASelectablesFacade facade = FSelectablesFacade.get(getActivity().getApplication(), this);
            facade.addFeedbackListener((tipo, feedback, args) -> {
                mPositiveAdapter.updateDataset(feedback);
            });
            facade.addObserver((tipo, feedback) ->{
                mNegativeAdapter.updateDataset(feedback);
            });
            facade.getPositiveSelectablesOf(mTimeBlock.getId());
            facade.getNegativeSelectablesOf(mTimeBlock.getId());
        } else {
            IPositiveAsSelectable pSel = FDbChecksAsSelectable.getPositive(getActivity().getApplication(), this);
            INegativeAsSelectable nSel = FDbChecksAsSelectable.getNegative(getActivity().getApplication(), this);
            pSel.addObserver((tipo, feedback) -> {
                mPositiveAdapter.updateDataset(feedback);
            });
            nSel.addObserver((tipo, feedback) -> {
                mNegativeAdapter.updateDataset(feedback);
            });
            pSel.getPositiveSelectables("tag for callback positives");
            nSel.getNegativeSelectables("tag for callback negatives");
        }

        mPositiveRecycler.setAdapter(mPositiveAdapter);
        mNegativeRecycler.setAdapter(mNegativeAdapter);

        return mView;
    }

    private void fillFieldsWithExistingData(AbstractTimeBlock timeBlock) {
        mName.setText(timeBlock.getName());
        fromH = (int) (mTimeBlock.getFromTime()/(60*60*1000));
        fromM = (int) (mTimeBlock.getFromTime()%(60*60*1000))/(60*1000);
        toH = (int) (mTimeBlock.getFromTime()/(60*60*1000));
        toM = (int) (mTimeBlock.getFromTime()%(60*60*1000))/(60*1000);
        fillDays(timeBlock);
    }

    private void guardar(View v) {
        mTimeBlock.setName(mName.getText().toString());
        mTimeBlock.setFromTime(fromH*60L*60L*1000L + fromM*60L*1000L);
        mTimeBlock.setToTime(toH*60L*60L*1000L + toM*60L*1000L);
        mTimeBlock.setMinimumLapse(initH*60L*60L*1000L + initM*60L*1000L);
        mTimeBlock.setMaximumLapse(finH*60L*60L*1000L + finM*60L*1000L);
        mTimeBlock.setDays(getDays());
        mTimeBlock.setNegativeChecks(new ArrayList<>(mNegativeAdapter.getSelectedFromDataSet()));
        mTimeBlock.setPositiveChecks(new ArrayList<>(mPositiveAdapter.getSelectedFromDataSet()));

        if (assertValid()) {
            doCallBack(mCurrentFeedback, mTimeBlock);
        }
    }

    private List<Integer> getDays() {
        List<Integer> days = new ArrayList<>();
        if (mLunesCheck.isChecked()) {days.add(0);}
        if (mMartesCheck.isChecked()) {days.add(1);}
        if (mMiercolesCheck.isChecked()) {days.add(2);}
        if (mJuevesCheck.isChecked()) {days.add(3);}
        if (mViernesCheck.isChecked()) {days.add(4);}
        if (mSabadoCheck.isChecked()) {days.add(5);}
        if (mDomingoCheck.isChecked()) {days.add(6);}
        return days;
    }

    private void fillDays(AbstractTimeBlock timeBlock) {
        Set<Integer> sDays = timeBlock.getDays().stream().collect(Collectors.toSet());
        if (sDays.contains(0)) {mLunesCheck.setChecked(true);}
        if (sDays.contains(1)) {mMartesCheck.setChecked(true);}
        if (sDays.contains(2)) {mMiercolesCheck.setChecked(true);}
        if (sDays.contains(3)) {mJuevesCheck.setChecked(true);}
        if (sDays.contains(4)) {mViernesCheck.setChecked(true);}
        if (sDays.contains(5)) {mSabadoCheck.setChecked(true);}
        if (sDays.contains(6)) {mDomingoCheck.setChecked(true);}
    }

    private boolean assertValid() {
        boolean valid = true;
        if (initH+initM*60 > finH+finM*60) {
            valid = false;
            red(mControlMin);
            red(mControlMax);
            red(mView.findViewById(R.id.textView28));
        } else {
            green(mControlMin);
            green(mControlMax);
            green(mView.findViewById(R.id.textView28));
        }
        if (mName.getText().toString().isEmpty()){
            red(mName);
            valid = false;
        } else {
            green(mName);
        }
        return valid;
    }

    private void red(View v) {
        v.setBackgroundColor(Color.RED);
    }

    private void green(View v) {
        v.setBackgroundColor(Color.TRANSPARENT);
    }
}
