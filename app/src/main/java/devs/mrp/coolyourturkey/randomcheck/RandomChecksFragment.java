package devs.mrp.coolyourturkey.randomcheck;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;

public class RandomChecksFragment extends Fragment implements MyObservable<Object> {

    public static final String FEEDBACK_TIME_BLOCKS = "feedback_time_blocks_for_random_checks_click";
    public static final String FEEDBACK_POSITIVE_CHECKS = "feedback_positive_checks_button_click";
    public static final String FEEDBACK_NEGATIVE_CHECKS = "feedback_negative_checks_click";

    private List<MyObserver<Object>> observers = new ArrayList<>();

    private Context mContext;

    private Button mTimeBlocksButton;
    private Button mPositiveChecksButton;
    private Button mNegativeChecksButton;
    private Button mSonidoButton;

    @Override
    public void addObserver(MyObserver<Object> observer) {
        observers.add(observer);
    }

    @Override
    public void doCallBack(String tipo, Object feedback) {
        observers.stream().forEach(o -> {
            o.callback(tipo, feedback);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_random_checks, container, false);

        mTimeBlocksButton = v.findViewById(R.id.buttonTimeBlocks);
        mPositiveChecksButton = v.findViewById(R.id.buttonPositiveChecks);
        mNegativeChecksButton = v.findViewById(R.id.buttonNegativeChecks);
        mSonidoButton = v.findViewById(R.id.buttonSonidoNotif);

        mTimeBlocksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCallBack(FEEDBACK_TIME_BLOCKS, null);
            }
        });

        mPositiveChecksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCallBack(FEEDBACK_POSITIVE_CHECKS, null);
            }
        });

        mNegativeChecksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCallBack(FEEDBACK_NEGATIVE_CHECKS, null);
            }
        });

        return v;
    }
}
