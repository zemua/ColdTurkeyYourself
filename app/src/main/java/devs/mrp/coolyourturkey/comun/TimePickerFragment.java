package devs.mrp.coolyourturkey.comun;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String EXTRA_REPLY_HORA = "Extra.reply.hora.time.picker.fragment.java";
    public static final String EXTRA_REPLY_MINUTO = "Extra.reply.minuto.time.picker.fragment.java";
    public static final String EXTRA_REPLY_STRING = "Extra.reply.string.time.picker.fragment.java";

    MisPreferencias mMisPreferencias;
    private int hour;
    private int minute;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMisPreferencias = new MisPreferencias(this.getContext());
        hour = (int) mMisPreferencias.getHoraInicioToqueDeQueda();
        minute = (int) mMisPreferencias.getMinutoInicioToqueDeQueda();

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        sendResult(Activity.RESULT_OK, this.hour, this.minute);
    }

    private void sendResult(int resultCode, int hora, int minuto) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_REPLY_HORA, hora);
        intent.putExtra(EXTRA_REPLY_MINUTO, minuto);
        Long milis = (hora * 60L * 60L * 1000L) + (minuto * 60 * 1000);
        intent.putExtra(EXTRA_REPLY_STRING, MilisToTime.getFormatedHM(milis));
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
