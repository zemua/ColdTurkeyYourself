package devs.mrp.coolyourturkey.randomcheck.timeblocks.review;

import android.content.Context;
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

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.databaseroom.randomchecks.SelectableFacade;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;
import devs.mrp.coolyourturkey.dtos.timeblock.FTimeBlockWithSelectableChecks;

public abstract class ATimeBlocksFragment<T extends AbstractTimeBlock> extends Fragment implements MyObservable<T> {

    protected List<MyObserver<T>> observers = new ArrayList<>();

    public static final String FEEDBACK_SAVE_NEW = "nuevo";
    public static final String FEEDBACK_SAVE_EXISTING = "existente";
    public static final String FEEDBACK_DELETE_THIS = "delete";

    protected Context mContext;
    protected T mTimeBlock;
    protected String mCurrent;

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

    // TODO add fields of view

    @Override
    public void addObserver(MyObserver<T> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, T feedback) {
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

        doStuffAfterMappingFields();


        LinearLayoutManager layout1 = new LinearLayoutManager(mContext);
        mNegativeRecycler.setLayoutManager(layout1);

        LinearLayoutManager layout2 = new LinearLayoutManager(mContext);
        mPositiveRecycler.setLayoutManager(layout2);

        SelectableFacade facade = new SelectableFacade(getActivity().getApplication(), this);


        doStuffAfterInitializingFields();

        return mView;
    }

    private AbstractTimeBlock buildObjectToReturn() {
        // TODO

        extrasToDoOnBuildingReturnObject();

        return null;
    }

    protected abstract void doStuffAfterMappingFields();

    protected abstract void doStuffAfterInitializingFields();

    protected abstract void extrasToDoOnBuildingReturnObject();
}
