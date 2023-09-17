package devs.mrp.coolyourturkey.randomcheck;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.DialogWithDelayPresenter;
import devs.mrp.coolyourturkey.comun.MyObservable;
import devs.mrp.coolyourturkey.comun.MyObserver;
import devs.mrp.coolyourturkey.comun.PermisosChecker;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.watchdog.checkscheduling.RandomCheckWorker;

@AndroidEntryPoint
public class RandomChecksFragment extends Fragment implements MyObservable<Object> {

    //private final int AUDIO_REQUEST = 0;

    public static final String FEEDBACK_TIME_BLOCKS = "feedback_time_blocks_for_random_checks_click";
    public static final String FEEDBACK_POSITIVE_CHECKS = "feedback_positive_checks_button_click";
    public static final String FEEDBACK_NEGATIVE_CHECKS = "feedback_negative_checks_click";

    private static final String NOTIFICATIONS_REQUEST_KEY = "notification.permissions.request.key";
    private static final String NOTIFICATIONS_CHANNEL_KEY = "notification.permissions.channel.key";

    private List<MyObserver<Object>> observers = new ArrayList<>();

    private Context mContext;
    private MisPreferencias misPreferencias;

    private Button mTimeBlocksButton;
    private Button mPositiveChecksButton;
    private Button mNegativeChecksButton;
    private Button mSonidoButton;

    @Inject
    @Named("zeroDelay")
    protected DialogWithDelayPresenter dialogWithDelayPresenter;

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

        mSonidoButton.setOnClickListener(view -> startChannelIntent());

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        showNotificationPermissionDialogs();
    }

    private void showNotificationPermissionDialogs() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (!hasNotificationPermissions()) {
                dialogWithDelayPresenter.setListener(NOTIFICATIONS_REQUEST_KEY, result -> {
                    if (result) {
                        PermisosChecker.requestPermisoNotificaciones(this.getContext());
                    }
                });
                dialogWithDelayPresenter.showDialog(NOTIFICATIONS_REQUEST_KEY,
                        this.getString(R.string.notificaciones),
                        this.getString(R.string.debes_activar_notificaciones_reasoning),
                        android.R.drawable.ic_popup_reminder);
            } else if (!isNotificationChannelActive()) {
                dialogWithDelayPresenter.setListener(NOTIFICATIONS_CHANNEL_KEY, result -> {
                    if (result) {
                        startChannelIntent();
                    }
                });
                dialogWithDelayPresenter.showDialog(NOTIFICATIONS_CHANNEL_KEY,
                        this.getString(R.string.canal_notificaciones),
                        this.getString(R.string.random_check_notification_channel_reasoning),
                        android.R.drawable.ic_popup_reminder);
            }
        }
    }

    private boolean hasNotificationPermissions() {
        return ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isNotificationChannelActive() {
        NotificationManager manager = (NotificationManager) this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = manager.getNotificationChannel(RandomCheckWorker.NOTIFICATION_CHANNEL_ID);
        return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
    }

    private void startChannelIntent() {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getApplicationInfo().packageName);
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, RandomCheckWorker.NOTIFICATION_CHANNEL_ID);
        startActivity(intent);
    }
}
