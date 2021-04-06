package devs.mrp.coolyourturkey.listados;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.listados.AplicacionListada;
import devs.mrp.coolyourturkey.plantillas.FeedbackListener;
import devs.mrp.coolyourturkey.plantillas.Feedbacker;

import java.util.ArrayList;
import java.util.List;

public class WaiterConfirmDialog extends DialogFragment implements Feedbacker<AlertDialog> {

    private static final String TAG = "Waiter Confirm Dialog";

    private static final String KEY_BUNDLE_TIEMPO = "key.bundle.tiempo";
    private static final String KEY_BUNDLE_MENSAJE = "key.bundle.mensaje";
    private static final String KEY_BUNDLE_POSICION = "key.bundle.posicion";
    private static final String KEY_BUNDLE_TITLE = "key.bundle.title";
    private static final String KEY_BUNDLE_APPINFO = "key.bundle.appinfo";
    private static final String KEY_BUNDLE_APPNOMBRE = "key.bundle.appnombre";
    private static final String KEY_BUNDLE_APPLISTA = "key.bundle.applista";

    private List<FeedbackListener<AlertDialog>> listeners = new ArrayList<>();
    public static final int FEEDBACK_ALERT_DIALOG = 0;

    public static final String EXTRA_RESPUESTA = "Confirmamos que no hacemos trampa";
    public static final String EXTRA_POSICION = "posicion en el dataset";
    public static final String EXTRA_NOMBRE = "extra nombre";
    public static final String EXTRA_LISTA = "extra lista";

    public static final int CUENTA_ATRAS_SEGUNDOS = 30;

    private String mMensaje;
    private AplicacionListada mApp;
    private Integer mPosicion;
    private Drawable mIcon;
    private String mTitle;
    private AlertDialog mDialogo;
    private Integer mTiempo;
    private DialogTimeUpdater mDialogTimeUpdater;
    private ApplicationInfo mAppInfo;
    private String mAppNombre;
    private String mAppLista;
    private boolean reConst = false;

    public WaiterConfirmDialog(){
        super();
        reConst = true;
    }

    public WaiterConfirmDialog(String mensaje, String title, Integer posicion, ApplicationInfo appInfo, String appNombre, String appLista){
        super();
        mMensaje = mensaje;
        mTiempo = CUENTA_ATRAS_SEGUNDOS;
        mPosicion = posicion;
        mTitle = title;
        mAppInfo = appInfo;
        mAppNombre = appNombre;
        mAppLista = appLista;
        reConst = false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        if (reConst){
            mMensaje = savedInstanceState.getString(KEY_BUNDLE_MENSAJE);
            mTiempo = savedInstanceState.getInt(KEY_BUNDLE_TIEMPO);
            mPosicion = savedInstanceState.getInt(KEY_BUNDLE_POSICION);
            mTitle = savedInstanceState.getString(KEY_BUNDLE_TITLE);
            mAppInfo = savedInstanceState.getParcelable(KEY_BUNDLE_APPINFO);
            mAppNombre = savedInstanceState.getString(KEY_BUNDLE_APPNOMBRE);
            mAppLista = savedInstanceState.getString(KEY_BUNDLE_APPLISTA);
        }

        mIcon = getActivity().getPackageManager().getApplicationIcon(mAppInfo);

        if (mTitle != null && mIcon != null) {
            mDialogo = new AlertDialog.Builder(getActivity())
                    .setIcon(mIcon)
                    .setTitle(mTitle)
                    .setMessage(mMensaje)
                    /* .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResult(Activity.RESULT_OK, true);
                        }
                    }) */
                    .setPositiveButton(String.valueOf(mTiempo), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // nada hasta que la cuenta llegue a cero
                        }
                    })
                    .setNegativeButton(R.string.rechazar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResult(Activity.RESULT_CANCELED, false);
                        }
                    })
                    .create();
                    /* d.setButton(Dialog.BUTTON_POSITIVE, "9", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // nada hasta que la cuenta llegue a cero
                        }
                    }); */

                    giveFeedback(FEEDBACK_ALERT_DIALOG, mDialogo);

                    return mDialogo;
        } else {
            Log.d(TAG, "hay valores nulos para el icono/titulo");
        }
        return null;
    }

    @Override
    public void onResume(){
        super.onResume();

        mDialogTimeUpdater = new DialogTimeUpdater(mDialogo, mTiempo, getActivity().getString(R.string.aceptar), getActivity());
        mDialogTimeUpdater.go();
        mDialogTimeUpdater.addFeedbackListener(new FeedbackListener<Integer>() {
            @Override
            public void giveFeedback(int tipo, Integer feedback, Object... args) {
                if (tipo == DialogTimeUpdater.FEEDBACK_OK) {
                    pulsadoAceptar();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        outstate.putInt(KEY_BUNDLE_TIEMPO, mDialogTimeUpdater.getTiempo());
        outstate.putString(KEY_BUNDLE_MENSAJE, mMensaje);
        outstate.putInt(KEY_BUNDLE_POSICION, mPosicion);
        outstate.putString(KEY_BUNDLE_TITLE, mTitle);
        outstate.putParcelable(KEY_BUNDLE_APPINFO, mAppInfo);
        outstate.putString(KEY_BUNDLE_APPNOMBRE, mAppNombre);
        outstate.putString(KEY_BUNDLE_APPLISTA, mAppLista);

        super.onSaveInstanceState(outstate);
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        sendResult(Activity.RESULT_CANCELED, false);
    }

    @Override
    public void onCancel(DialogInterface dialog){
        super.onCancel(dialog);
        sendResult(Activity.RESULT_CANCELED, false);
    }

    public void pulsadoAceptar(){
        sendResult(Activity.RESULT_OK, true);
    }

    private void sendResult(int resultCode, boolean aceptado){
        if (getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESPUESTA, aceptado);
        if (mPosicion != null && mAppNombre != null && mAppLista != null) {
            intent.putExtra(EXTRA_NOMBRE, mAppNombre);
            intent.putExtra(EXTRA_LISTA, mAppLista);
            intent.putExtra(EXTRA_POSICION, mPosicion);
        }
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @Override
    public void giveFeedback(int tipo, AlertDialog feedback) {
        listeners.forEach((listener)->{
            listener.giveFeedback(tipo, feedback);
        });
    }

    @Override
    public void addFeedbackListener(FeedbackListener<AlertDialog> listener) {
        listeners.add(listener);
    }
}
