package devs.mrp.coolyourturkey.randomcheck.positivecheck;

import android.content.Context;
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
import devs.mrp.coolyourturkey.dtos.randomcheck.CheckFactory;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

public class PositiveChecksFragment extends Fragment implements MyObservable<PositiveCheck> {

    private List<MyObserver<PositiveCheck>> listeners = new ArrayList<>();

    public static final String FEEDBACK_SAVE_NEW = "nuevo";
    public static final String FEEDBACK_SAVE_EXISTING = "existente";

    private final int MAX_MULTIPLIER = 4;

    private Context mContext;
    private PositiveCheck mCheck;
    private String mCurrent;

    private EditText mNameText;
    private EditText mQuestionText;
    private Button mMinusButton;
    private Button mPlusButton;
    private TextView mMultiplierText;
    private Button mSaveButton;

    @Override
    public void doCallBack(String tipo, PositiveCheck feedback) {
        listeners.stream().forEach(l -> {
            l.callback(tipo, feedback);
        });
    }

    @Override
    public void addObserver(MyObserver<PositiveCheck> listener) {
        listeners.add(listener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_positive_checks, container, false);

        mNameText = v.findViewById(R.id.editTextName);
        mQuestionText = v.findViewById(R.id.editTextPregunta);
        mMinusButton = v.findViewById(R.id.buttonMinus);
        mPlusButton = v.findViewById(R.id.buttonPlus);
        mMultiplierText = v.findViewById(R.id.textMultiplier);
        mSaveButton = v.findViewById(R.id.buttonGuardar);

        if (mCheck == null) {
            mCurrent = FEEDBACK_SAVE_NEW;
            CheckFactory factory = new CheckFactory();
            mCheck = factory.newPositive();
            mMultiplierText.setText("1");
        } else {
            mCurrent = FEEDBACK_SAVE_EXISTING;
            fillFields();
        }
        if (mCheck.getMultiplicador() == null) {
            mCheck.setMultiplicador(1);
        }

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase();
            }
        });

        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });


        return v;
    }

    public void setCheck(PositiveCheck c) {
        mCheck = c;
    }

    private void fillFields() {
        mNameText.setText(mCheck.getName());
        mQuestionText.setText(mCheck.getQuestion());
        mMultiplierText.setText(mCheck.getMultiplicador());
    }

    private void increase() {
        Integer i = mCheck.getMultiplicador();
        if (i >= MAX_MULTIPLIER) {
            return;
        }
        mMultiplierText.setText(String.valueOf(i+1));
        mCheck.setMultiplicador(i+1);
    }

    private void decrease() {
        Integer i = mCheck.getMultiplicador();
        if (i <= 1) {
            mMultiplierText.setText("1");
            mCheck.setMultiplicador(1);
            return;
        }
        mMultiplierText.setText(String.valueOf(i-1));
        mCheck.setMultiplicador(i-1);
    }

    private void guardar() {
        mCheck.setName(mNameText.getText().toString());
        mCheck.setQuestion(mQuestionText.getText().toString());
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
        return valid;
    }

    private void red(View v) {
        v.setBackgroundColor(Color.RED);
    }

    private void green(View v) {
        v.setBackgroundColor(Color.TRANSPARENT);
    }
}
