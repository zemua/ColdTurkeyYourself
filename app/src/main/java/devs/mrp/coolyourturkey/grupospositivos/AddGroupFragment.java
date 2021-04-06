package devs.mrp.coolyourturkey.grupospositivos;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.plantillas.FeedbackReceiver;

public class AddGroupFragment extends Fragment {

    public static final int FEEDBACK_NAMED = 0;

    private Context mContext;
    private FeedbackReceiver<Fragment, Object> mFeedbackReceiver;

    private Button mAddButton;
    private EditText mNombreGrupo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        mFeedbackReceiver = (FeedbackReceiver) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addgroup, container, false);

        mAddButton = (Button) v.findViewById(R.id.buttonadd);
        mNombreGrupo = (EditText) v.findViewById(R.id.editTextTextGroupName);

        mAddButton.setEnabled(false);

        mNombreGrupo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 && mAddButton.isEnabled()) {
                    mAddButton.setEnabled(false);
                } else if(!mAddButton.isEnabled()) {
                    mAddButton.setEnabled(true);
                }
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackReceiver.receiveFeedback(AddGroupFragment.this, FEEDBACK_NAMED, mNombreGrupo.getText());
            }
        });

        return v;
    }

}
