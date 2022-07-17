package devs.mrp.coolyourturkey.watchdog.utils.impl;

import android.content.Context;

import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.time.temporal.ChronoField;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.configuracion.MisPreferencias;
import devs.mrp.coolyourturkey.watchdog.utils.Notifier;

public class ChangeOfDayNotifier implements Notifier<Object> {

    private MisPreferencias prefs;
    private Notifier<String> notifier;
    private Context context;

    public ChangeOfDayNotifier(Context context) {
        this.context = context;
        this.prefs = new MisPreferencias(context);
        this.notifier = new CachedNotifier(context);
    }

    @Override
    public void notify(@Nullable Object data) {
        if (!prefs.getNotifyChangeOfDay()) {
            return;
        }
        long notifyPeriod = MilisToTime.getMilisDeMinutos(prefs.getMinutesNotifyChangeOfDay());
        long changeOfDay = MilisToTime.getMilisDeHoras(prefs.getHourForChangeOfDay());
        long now = LocalTime.now().getLong(ChronoField.MILLI_OF_DAY);
        if (now < changeOfDay) {
            if (changeOfDay - now < notifyPeriod) {
                notifier.notify(context.getString(R.string.you_are_close_to_the_change_of_day));
            }
        } else {
            if (changeOfDay + MilisToTime.getMilisDeHoras(24) - now < notifyPeriod) {
                notifier.notify(context.getString(R.string.you_are_close_to_the_change_of_day));
            }
        }
    }
}
