package devs.mrp.coolyourturkey.randomcheck;

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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;

public abstract class AbstractChecksFragment<T extends Check> extends Fragment implements MyObservable<T> {

    protected List<MyObserver<T>> listeners = new ArrayList<>();

    public static final String FEEDBACK_SAVE_NEW = "nuevo";
    public static final String FEEDBACK_SAVE_EXISTING = "existente";
    public static final String FEEDBACK_DELETE_THIS = "delete";

    protected Context mContext;
    protected T mCheck;
    protected String mCurrent;

    protected EditText mNameText;
    protected EditText mQuestionText;
    protected Button mSaveButton;
    protected Button mDeleteButton;

    protected TextView mFrequencyText;
    protected Button mFrequencyPlus;
    protected Button mFrequencyMinus;
    protected int frequency = 1;
    private static final int MAX_FREQUENCY = 50;

    protected View mView;

    @Override
    public void doCallBack(String tipo, T feedback) {
        listeners.stream().forEach(l -> {
            l.callback(tipo, feedback);
        });
    }

    @Override
    public void addObserver(MyObserver<T> listener) {
        listeners.add(listener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_positive_checks, container, false);

        mNameText = mView.findViewById(R.id.editTextName);
        mQuestionText = mView.findViewById(R.id.editTextPregunta);
        mSaveButton = mView.findViewById(R.id.buttonGuardar);
        mDeleteButton = mView.findViewById(R.id.buttonDel);
        mFrequencyText = mView.findViewById(R.id.frequencytext);
        mFrequencyMinus = mView.findViewById(R.id.frequencyminus);
        mFrequencyPlus = mView.findViewById(R.id.frequencyplus);
        initializeOtherFields(mView);

        setNameHint(mNameText);
        setQuestionHint(mQuestionText);

        if (mCheck == null) {
            mCurrent = FEEDBACK_SAVE_NEW;
            mCheck = getNewCheck();
            mDeleteButton.setVisibility(View.GONE);
            doStuffIfNew();
        } else {
            mCurrent = FEEDBACK_SAVE_EXISTING;
            mDeleteButton.setVisibility(View.VISIBLE);
            fillFields();
            doStuffIfExisting();
        }

        mFrequencyMinus.setOnClickListener(v -> {
            if (frequency > 1) {
                frequency --;
                mFrequencyText.setText(String.valueOf(frequency));
            }
        });

        mFrequencyPlus.setOnClickListener(v -> {
            if (frequency < MAX_FREQUENCY) {
                frequency ++;
                mFrequencyText.setText(String.valueOf(frequency));
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.confirmacion);
                builder.setMessage(R.string.seguro_que_deseas_borrar_este_control);
                builder.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doCallBack(FEEDBACK_DELETE_THIS, mCheck);
                    }
                });
                builder.setNegativeButton(R.string.conservar, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        setupOtherObservers();

        return mView;
    }

    public void setCheck(T c) {
        mCheck = c;
    }

    protected abstract void initializeOtherFields(View v);

    protected abstract void doStuffIfNew();

    protected abstract void doStuffIfExisting();

    private void fillFields() {
        mNameText.setText(mCheck.getName());
        mQuestionText.setText(mCheck.getQuestion());
        mFrequencyText.setText(String.valueOf(mCheck.getFrequency()));
        frequency = mCheck.getFrequency();
        fillOtherFields(mView);
    }

    protected abstract void fillOtherFields(View v);

    private void guardar() {
        mCheck.setName(mNameText.getText().toString());
        mCheck.setQuestion(mQuestionText.getText().toString());
        mCheck.setFrequency(frequency);
        if (assertValid()) {
            doCallBack(mCurrent, mCheck);
        }
    }

    private boolean assertValid() {
        boolean valid = true;
        if (mCheck.getName().isEmpty()){
            red(mNameText);
            valid = false;
        } else {
            green(mNameText);
        }
        if(mCheck.getQuestion().isEmpty()) {
            red(mQuestionText);
            valid = false;
        } else {
            green(mQuestionText);
        }
        if (!assertOtherValidFields()){
            valid = false;
        }
        return valid;
    }

    protected abstract boolean assertOtherValidFields();

    protected abstract void setNameHint(EditText name);

    protected abstract void setQuestionHint(EditText question);

    private void red(View v) {
        v.setBackgroundColor(Color.RED);
    }

    private void green(View v) {
        v.setBackgroundColor(Color.TRANSPARENT);
    }

    protected abstract T getNewCheck();

    protected abstract void setupOtherObservers();
}
