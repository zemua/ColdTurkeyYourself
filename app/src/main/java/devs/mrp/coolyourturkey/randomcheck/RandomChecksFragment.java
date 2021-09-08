package devs.mrp.coolyourturkey.randomcheck;

import static android.media.RingtoneManager.EXTRA_RINGTONE_TYPE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.watchdog.checkscheduling.RandomCheckWorker;

public class RandomChecksFragment extends Fragment implements MyObservable<Object> {

    //private final int AUDIO_REQUEST = 0;

    public static final String FEEDBACK_TIME_BLOCKS = "feedback_time_blocks_for_random_checks_click";
    public static final String FEEDBACK_POSITIVE_CHECKS = "feedback_positive_checks_button_click";
    public static final String FEEDBACK_NEGATIVE_CHECKS = "feedback_negative_checks_click";

    private List<MyObserver<Object>> observers = new ArrayList<>();

    private Context mContext;
    private MisPreferencias misPreferencias;

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
        misPreferencias = new MisPreferencias(context);
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

        mSonidoButton.setOnClickListener(view -> {
            //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            //Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
            //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            //startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.escoge_audio)), AUDIO_REQUEST);

            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getApplicationInfo().packageName);
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, RandomCheckWorker.NOTIFICATION_CHANNEL_ID);
            startActivity(intent);
        });

        return v;
    }

    //@Override
    //public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (requestCode == AUDIO_REQUEST) {
        //    if (resultCode == Activity.RESULT_OK) {
        //        Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
        //        if (uri != null) {
        //            misPreferencias.setRandomCheckSound(uri);
        //        }
        //    }
        //}
        //super.onActivityResult(requestCode, resultCode, data);
    //}
}
