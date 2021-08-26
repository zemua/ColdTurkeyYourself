package devs.mrp.coolyourturkey.randomcheck.positivecheck.review;

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

import androidx.constraintlayout.widget.ConstraintLayout;
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
import devs.mrp.coolyourturkey.randomcheck.AbstractChecksFragment;

public class PositiveChecksFragment extends AbstractChecksFragment<PositiveCheck> {

    protected final int MAX_MULTIPLIER = 4;

    private Button mMinusButton;
    private Button mPlusButton;
    private TextView mMultiplierText;

    @Override
    protected void initializeOtherFields(View v) {
        mMultiplierText = v.findViewById(R.id.textMultiplier);
        mMinusButton = v.findViewById(R.id.buttonMinus);
        mPlusButton = v.findViewById(R.id.buttonPlus);

        ConstraintLayout positiveStuff = v.findViewById(R.id.positiveStuff);
        positiveStuff.setVisibility(View.VISIBLE);
    }

    @Override
    protected void doStuffIfNew() {
        mCheck.setMultiplicador(1);
        mMultiplierText.setText("1");
    }

    @Override
    protected void doStuffIfExisting() {

    }

    @Override
    protected void fillOtherFields(View v) {
        mMultiplierText.setText(String.valueOf(mCheck.getMultiplicador()));
    }

    @Override
    protected boolean assertOtherValidFields() {
        return true;
    }

    @Override
    protected PositiveCheck getNewCheck() {
        return new CheckFactory().newPositive();
    }

    @Override
    protected void setupOtherObservers() {
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
}
