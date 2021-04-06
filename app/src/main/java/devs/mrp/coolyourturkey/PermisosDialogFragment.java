package devs.mrp.coolyourturkey;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import devs.mrp.coolyourturkey.R;

public class PermisosDialogFragment extends DialogFragment {

    private static final String ARG_PERMISO = "permiso";
    public static final String EXTRA_PERMISO = "PermisosDialogFragment permiso";

    private String mMensaje = "sin mensaje";
    private String mTitulo = "sin titulo";

    public void setMensaje(String mensaje){
        mMensaje = mensaje;
    }

    public PermisosDialogFragment(String titulo, String mensaje){
        mTitulo = titulo;
        mMensaje = mensaje;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new AlertDialog.Builder(getActivity())
                .setTitle(mTitulo)
                .setMessage(mMensaje)
                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, true);
                    }
                })
                .setNegativeButton(R.string.rechazar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, false);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, boolean aceptado){
        if (getTargetFragment() == null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PERMISO, aceptado);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
