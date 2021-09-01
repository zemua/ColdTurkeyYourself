package devs.mrp.coolyourturkey.randomcheck.timeblocks.review;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.FTimePicker;
import devs.mrp.coolyourturkey.comun.IMyTimePicker;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.comun.ObjectWrapperForBinder;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.ASelectablesFacade;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.FDbChecksAsSelectable;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.FSelectablesFacade;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.INegativeAsSelectable;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.IPositiveAsSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.ANegativeCheckSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.APositiveCheckSelectable;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.TimeBlockFactory;
import devs.mrp.coolyourturkey.randomcheck.timeblocks.pickerchain.PickerCommander;

public class TimeBlocksFragment extends Fragment implements MyObservable<AbstractTimeBlock> {

    public static final String TAG = "TimeBlocksFragment";

    protected List<MyObserver<AbstractTimeBlock>> observers = new ArrayList<>();

    public static final String FEEDBACK_SAVE_NEW = "nuevo";
    public static final String FEEDBACK_SAVE_EXISTING = "existente";
    public static final String FEEDBACK_DELETE_THIS = "delete";

    public static final int REQUEST_CODE_DESDE_HORA = 0;
    public static final int REQUEST_CODE_HASTA_HORA = 1;
    public static final int REQUEST_CODE_MIN_TIME = 2;
    public static final int REQUEST_CODE_MAX_TIME = 3;

    private static final String KEY_BUNDLE_MIN_H = "min h";
    private static final String KEY_BUNDLE_MIN_M = "min m";
    private static final String KEY_BUNDLE_MAX_H = "max h";
    private static final String KEY_BUNDLE_MAX_M = "max m";

    private static final String KEY_BUNDLE_FROM_H = "from h";
    private static final String KEY_BUNDLE_FROM_M = "from m";
    private static final String KEY_BUNDLE_TO_H = "to h";
    private static final String KEY_BUNDLE_TO_M = "to m";

    private static final String KEY_BUNDLE_POSITIVE_CHECKED = "positive checked";
    private static final String KEY_BUNDLE_NEGATIVE_CHECKED = "negative checked";

    private static final String KEY_BUNDLE_TIME_BLOCK_ENTITY = "time block entity";

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

    protected int minH = 0;
    protected int minM = 0;
    protected int maxH = 0;
    protected int maxM = 0;

    protected int fromH = 0;
    protected int fromM = 0;
    protected int toH = 0;
    protected int toM = 0;

    protected Supplier<IMyTimePicker> pickers = FTimePicker::getNuevo;

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
    public void onSaveInstanceState(Bundle outState) {
        outState.putBinder(KEY_BUNDLE_POSITIVE_CHECKED, new ObjectWrapperForBinder(mPositiveAdapter.getFullDataSet()));
        outState.putBinder(KEY_BUNDLE_NEGATIVE_CHECKED, new ObjectWrapperForBinder(mNegativeAdapter.getFullDataSet()));

        saveDataInTimeBlock();
        outState.putBinder(KEY_BUNDLE_TIME_BLOCK_ENTITY, new ObjectWrapperForBinder(mTimeBlock));
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

        mPositiveAdapter = new SelectablesAdapter<>();
        mNegativeAdapter = new SelectablesAdapter<>();

        boolean fromSavedInstance = false;

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            fromSavedInstance = true;
        }

        if (fromSavedInstance) {
            mTimeBlock = (AbstractTimeBlock) ((ObjectWrapperForBinder)savedInstanceState.getBinder(KEY_BUNDLE_TIME_BLOCK_ENTITY)).getData();

            mPositiveAdapter.updateDataset((List<APositiveCheckSelectable>)((ObjectWrapperForBinder) savedInstanceState.getBinder(KEY_BUNDLE_POSITIVE_CHECKED)).getData());
            mNegativeAdapter.updateDataset((List<ANegativeCheckSelectable>)((ObjectWrapperForBinder) savedInstanceState.getBinder(KEY_BUNDLE_NEGATIVE_CHECKED)).getData());
        }

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

        mPreHoraButton.setOnClickListener(view -> pickers.get().pick(this, REQUEST_CODE_DESDE_HORA, TAG));
        mPostHoraButton.setOnClickListener(view -> pickers.get().pick(this, REQUEST_CODE_HASTA_HORA, TAG));
        mControlMin.setOnClickListener(view -> pickers.get().pick(this, REQUEST_CODE_MIN_TIME, TAG));
        mControlMax.setOnClickListener(view -> pickers.get().pick(this, REQUEST_CODE_MAX_TIME, TAG));

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

        if (!fromSavedInstance) {
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
        }

        mPositiveRecycler.setAdapter(mPositiveAdapter);
        mNegativeRecycler.setAdapter(mNegativeAdapter);

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "llamado onActivityResult");
            new PickerCommander(mView, this).getHandlerChain().receiveRequest(String.valueOf(requestCode), resultData);
        }
    }

    private void fillFieldsWithExistingData(AbstractTimeBlock timeBlock) {
        Log.d(TAG, timeBlock.toString());
        mName.setText(timeBlock.getName());
        fromH = (int) (timeBlock.getFromTime()/(60*60*1000));
        fromM = (int) (timeBlock.getFromTime()%(60*60*1000))/(60*1000);
        toH = (int) (timeBlock.getToTime()/(60*60*1000));
        toM = (int) (timeBlock.getToTime()%(60*60*1000))/(60*1000);
        minH = (int) (timeBlock.getMinimumLapse()/(60*60*1000));
        minM = (int) (timeBlock.getMinimumLapse()%(60*60*1000)/(60*1000));
        maxH = (int) (timeBlock.getMaximumLapse()/(60*60*1000));
        maxM = (int) (timeBlock.getMaximumLapse()%(60*60*1000)/(60*1000));
        fillDays(timeBlock);

        setButtonsText();
    }

    private void setButtonsText() {
        mPreHoraButton.setText(fromH + ":" + fromM);
        mPostHoraButton.setText(toH + ":" + toM);
        mControlMin.setText(minH + ":" + minM);
        mControlMax.setText(maxH + ":" + maxM);
    }

    private void saveDataInTimeBlock() {
        mTimeBlock.setName(mName.getText().toString());
        mTimeBlock.setFromTime(fromH*60L*60L*1000L + fromM*60L*1000L);
        mTimeBlock.setToTime(toH*60L*60L*1000L + toM*60L*1000L);
        mTimeBlock.setMinimumLapse(minH *60L*60L*1000L + minM *60L*1000L);
        mTimeBlock.setMaximumLapse(maxH *60L*60L*1000L + maxM *60L*1000L);
        mTimeBlock.setDays(getDays());
        mTimeBlock.setNegativeChecks(new ArrayList<>(mNegativeAdapter.getSelectedFromDataSet()));
        mTimeBlock.setPositiveChecks(new ArrayList<>(mPositiveAdapter.getSelectedFromDataSet()));
    }

    private void guardar(View v) {
        saveDataInTimeBlock();

        Log.d(TAG, "checked negatives: " + mTimeBlock.getNegativeChecks());
        Log.d(TAG, "checked positives: " + mTimeBlock.getPositiveChecks());

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
        if (minH + minM *60 > maxH + maxM *60 || (minH == 0 && minM == 0)) {
            valid = false;
            red(mView.findViewById(R.id.textView28));
        } else {
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

    public int getMinH() {
        return minH;
    }

    public void setMinH(int minH) {
        this.minH = minH;
    }

    public int getMinM() {
        return minM;
    }

    public void setMinM(int minM) {
        this.minM = minM;
    }

    public int getMaxH() {
        return maxH;
    }

    public void setMaxH(int maxH) {
        this.maxH = maxH;
    }

    public int getMaxM() {
        return maxM;
    }

    public void setMaxM(int maxM) {
        this.maxM = maxM;
    }

    public int getFromH() {
        return fromH;
    }

    public void setFromH(int fromH) {
        this.fromH = fromH;
    }

    public int getFromM() {
        return fromM;
    }

    public void setFromM(int fromM) {
        this.fromM = fromM;
    }

    public int getToH() {
        return toH;
    }

    public void setToH(int toH) {
        this.toH = toH;
    }

    public int getToM() {
        return toM;
    }

    public void setToM(int toM) {
        this.toM = toM;
    }
}
