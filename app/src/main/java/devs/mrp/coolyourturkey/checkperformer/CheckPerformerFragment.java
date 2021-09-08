package devs.mrp.coolyourturkey.checkperformer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.DialogWithDelay;
import devs.mrp.coolyourturkey.comun.MyObservableNegative;
import devs.mrp.coolyourturkey.comun.MyObservablePositive;
import devs.mrp.coolyourturkey.comun.MyObserver;

public class CheckPerformerFragment extends Fragment implements MyObservableNegative<Boolean>, MyObservablePositive<Boolean> {

    private List<MyObserver<Boolean>> negativeObservers = new ArrayList<>();
    private List<MyObserver<Boolean>> positiveObservers = new ArrayList<>();

    private View mView;

    private String mQuestion;
    private boolean mNoWaits;
    private boolean mYesWaits;
    private boolean mFeedback;
    private Context mContext;

    public CheckPerformerFragment(String question, boolean noWaits, boolean yesWaits, Boolean feedback, Context context) {
        mQuestion = question;
        mNoWaits = noWaits;
        mYesWaits = yesWaits;
        mFeedback = feedback;
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_random_control, container, false);

        TextView pregunta = mView.findViewById(R.id.textPregunta);
        pregunta.setText(mQuestion);

        int delay = 0;
        int replyValue = 0; // not used, we set the listener directly on the call
        String tagDialog = "tagDialog";

        Button botonNo = mView.findViewById(R.id.button);
        if (mFeedback) {
            botonNo.setOnClickListener(view -> doNegativeCallback(mFeedback));
        } else {
            final DialogWithDelay dialog = new DialogWithDelay(R.drawable.seal, mContext.getString(R.string.confirmacion), mContext.getString(R.string.estas_seguro), replyValue, delay, (tipo, feedback, args) -> {
                if (tipo == DialogWithDelay.FEEDBACK_ALERT_DIALOG_ACEPTAR) {
                    doNegativeCallback(mFeedback);
                }
            });
            botonNo.setOnClickListener(view -> dialog.show(getActivity().getSupportFragmentManager(), tagDialog));
        }

        Button botonSi = mView.findViewById(R.id.button2);
        if (!mFeedback) {
            botonSi.setOnClickListener(view -> doPositiveCallback(mFeedback));
        } else {
            final DialogWithDelay dialog = new DialogWithDelay(R.drawable.seal, mContext.getString(R.string.confirmacion), mContext.getString(R.string.estas_seguro), replyValue, delay, (tipo, feedback, args) -> {
                if (tipo == DialogWithDelay.FEEDBACK_ALERT_DIALOG_ACEPTAR) {
                    doPositiveCallback(mFeedback);
                }
            });
            botonSi.setOnClickListener(view -> dialog.show(getActivity().getSupportFragmentManager(), tagDialog));
        }

        return mView;
    }

    @Override
    public void addNegativeObserver(MyObserver<Boolean> observer) {
        negativeObservers.add(observer);
    }

    @Override
    public void doNegativeCallback(Boolean feedback) {
        negativeObservers.forEach(o -> o.callback("no", mFeedback));
    }

    @Override
    public void addPositiveObserver(MyObserver<Boolean> observer) {
        positiveObservers.add(observer);
    }

    @Override
    public void doPositiveCallback(Boolean feedback) {
        positiveObservers.forEach(o -> o.callback("yes", mFeedback));
    }
}
