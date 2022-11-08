package devs.mrp.coolyourturkey.comun;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

import java.util.ArrayList;
import java.util.List;

public class DialogWithDelay extends DialogFragment implements Feedbacker<AlertDialog> {

    private static final String TAG = "Dialog With Delay";

    private List<FeedbackListener<AlertDialog>> listeners = new ArrayList<>();
    public static final int FEEDBACK_ALERT_DIALOG_ACEPTAR = 0;
    public static final int FEEDBACK_ALERT_DIALOG_RECHAZAR = 1;

    public static final String EXTRA_REPLY_VALUE = "Extra.reply.key.integer.dialog.with.delay.java";
    public static final String EXTRA_RESPUESTA = "Extra.respuesta.boolean.dialog.with.delay.java";

    private static final String KEY_BUNDLE_MENSAJE = "key.bundle.mensaje";
    private static final String KEY_BUNDLE_TITULO = "key.bundle.titulo";
    private static final String KEY_BUNDLE_REPLY_VALUE = "key.bundle.reply.value";
    private static final String KEY_BUNDLE_ICONO = "key.bundle.icono";
    private static final String KEY_BUNDLE_TIEMPO = "key.bundle.tiempo";

    private DialogDelayer mDialogDelayer;
    private String mMensaje;
    private Drawable mIcon;
    private int mIconResId;
    private String mTitle;
    private Integer mReplyValue;
    protected AlertDialog mDialogo;
    private Integer mTiempo;
    private Context mContext;
    private boolean restaurar = false;
    private boolean buttonPushed = false;

    public DialogWithDelay() {
        super();
        restaurar = true;
    }

    public DialogWithDelay(int iconResId, String title, String message) {
        this(iconResId, title, message, 0);
    }

    public DialogWithDelay(int iconResId, String title, String message, Integer replyValue) {
        super();
        restaurar = false;
        mIconResId = iconResId;
        mTitle = title;
        mMensaje = message;
        mReplyValue = replyValue;
        mTiempo = DialogDelayer.CUENTA_ATRAS_SEGUNDOS;
    }

    public DialogWithDelay(int iconResId, String title, String message, Integer replyValue, int delaySeconds, FeedbackListener<AlertDialog> listener) {
        super();
        restaurar = false;
        mIconResId = iconResId;
        mTitle = title;
        mMensaje = message;
        mReplyValue = replyValue;
        mTiempo = delaySeconds;
        addFeedbackListener(listener);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (restaurar) {
            restoreValues(savedInstanceState);
        }
        mIcon = getActivity().getDrawable(mIconResId);

        if (mTitle != null && mIcon != null) {
            mDialogo = new AlertDialog.Builder(getActivity())
                    .setIcon(mIcon)
                    .setTitle(mTitle)
                    .setMessage(mMensaje)
                    .setPositiveButton(String.valueOf(mTiempo), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // nada hasta que la cuenta llegue a cero
                        }
                    })
                    .setNegativeButton(R.string.rechazar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DialogWithDelay.this.sendResult(Activity.RESULT_CANCELED, false);
                        }
                    })
                    .create();

            // giveFeedback(FEEDBACK_ALERT_DIALOG, mDialogo);

            return mDialogo;
        } else {
            Log.d(TAG, "hay valores nulos para el icono/titulo");
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();

        if (mDialogDelayer != null) {
            mDialogDelayer.interrumpe();
        }

        mDialogDelayer = new DialogDelayer(mDialogo, mTiempo, getActivity().getString(R.string.aceptar), mContext);
        mDialogDelayer.addFeedbackListener(new FeedbackListener<Integer>() {
            @Override
            public void giveFeedback(int tipo, Integer feedback, Object... args) {
                if (tipo == DialogDelayer.FEEDBACK_OK) {
                    pulsadoAceptar();
                }
            }
        });
        mDialogDelayer.go();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDialogDelayer.interrumpe();
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        outstate.putString(KEY_BUNDLE_MENSAJE, mMensaje);
        outstate.putString(KEY_BUNDLE_TITULO, mTitle);
        outstate.putInt(KEY_BUNDLE_REPLY_VALUE, mReplyValue);
        outstate.putInt(KEY_BUNDLE_ICONO, mIconResId);
        outstate.putInt(KEY_BUNDLE_TIEMPO, mDialogDelayer.getTiempo());
        super.onSaveInstanceState(outstate);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!buttonPushed) {
            sendResult(Activity.RESULT_CANCELED, false);
        }
        buttonPushed = false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        buttonPushed = true;
        sendResult(Activity.RESULT_CANCELED, false);
    }

    protected void restoreValues(Bundle savedInstanceState) {
        mMensaje = savedInstanceState.getString(KEY_BUNDLE_MENSAJE);
        mTitle = savedInstanceState.getString(KEY_BUNDLE_TITULO);
        mReplyValue = savedInstanceState.getInt(KEY_BUNDLE_REPLY_VALUE);
        mIconResId = savedInstanceState.getInt(KEY_BUNDLE_ICONO);
        mTiempo = savedInstanceState.getInt(KEY_BUNDLE_TIEMPO);
    }

    private void pulsadoAceptar() {
        buttonPushed = true;
        sendResult(Activity.RESULT_OK, true);
    }

    protected void sendResult(int resultCode, boolean aceptado) {
        if (aceptado) {
            giveFeedback(FEEDBACK_ALERT_DIALOG_ACEPTAR, mDialogo);
        } else {
            giveFeedback(FEEDBACK_ALERT_DIALOG_RECHAZAR, mDialogo);
        }
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESPUESTA, aceptado);
        intent.putExtra(EXTRA_REPLY_VALUE, mReplyValue);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    public void setContext(Context c) {
        mContext = c;
    }

    @Override
    public void giveFeedback(int tipo, AlertDialog feedback) {
        listeners.forEach((listener) -> {
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<AlertDialog> listener) {
        listeners.add(listener);
    }
}
