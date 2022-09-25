package devs.mrp.coolyourturkey.comun;

import android.content.Context;
import android.icu.util.Calendar;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

import devs.mrp.coolyourturkey.configuracion.MisPreferencias;

public class MilisToTime {

    public static Long getHoras(Long milis){
        return milis/(1000*60*60);
    }

    public static Long getMinutos(Long milis){
        return (milis%(1000*60*60))/(1000*60);
    }

    public static Long getSegundos(Long milis){
        return (milis%(1000*60))/(1000);
    }

    public static String getFormated(Long milis){
        Formatter formatter = new Formatter();
        if (milis < 0){
            formatter.format("[ - %02d:%02d:%02d ]", getHoras(-milis), getMinutos(-milis), getSegundos(-milis));
        } else {
            formatter.format("%02d:%02d:%02d", getHoras(milis), getMinutos(milis), getSegundos(milis));
        }
        return formatter.toString();
    }

    public static String getFormatedHM(Long milis) {
        Formatter formatter = new Formatter();
        if (milis < 0){
            formatter.format("[ - %02d:%02d ]", getHoras(-milis), getMinutos(-milis));
        } else {
            formatter.format("%02d:%02d", getHoras(milis), getMinutos(milis));
        }
        return formatter.toString();
    }

    public static long getMilisDeSegundos(int segundos) {
        return segundos * 1000L;
    }

    public static long getMilisDeMinutos(int minutos){
        return minutos * 60L * 1000L;
    }

    public static long getMilisDeHoras(int horas) {
        return horas * 60L * 60L * 1000L;
    }

    public static long getDaysFromMilis(long milis) {return TimeUnit.MILLISECONDS.toDays(milis);}

    public static long milisDateToMilisTime(long milis) {
        // Calendar is legacy class
        /*Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milis);
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);*/

        ZonedDateTime zone = Instant.ofEpochMilli(milis)
                .atZone(ZoneId.systemDefault());
        int h = zone.getHour();
        int m = zone.getMinute();

        long nowToMilis = getMilisDeHoras(h) + getMilisDeMinutos(m);
        return nowToMilis;
    }

    /**
     * Time Conversion from millis
     * @param millis
     * @return
     */
    public static LocalDateTime millisToLocalDateTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date;
    }

    /**
     * Time conversion from millis
     * @param localDateTime
     * @return
     */
    public static long localDateTimeToMillis(LocalDateTime localDateTime) {
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    public static int milisToDayOfWeek(long milis) {
        ZonedDateTime zone = Instant.ofEpochMilli(milis)
                .atZone(ZoneId.systemDefault());
        return zone.getDayOfWeek().getValue();
    }

    public static long getHours(long millis) {
        return millis/(1000*60*60);
    }

    public static long getMinutes(long millis) {
        return (millis%(1000*60*60))/(1000*60);
    }

    public static long beginningOfTodayConsideringChangeOfDay(Context context) {
        return beginningOfOffsetDaysConsideringChangeOfDay(0, context);
    }

    public static long beginningOfOffsetDaysConsideringChangeOfDay(long offsetDays, Context context) {
        LocalDateTime start = beginningOfOffsetDaysConsideringChangeOfDayInLocalDateTime(offsetDays, context);
        ZonedDateTime zdt = start.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    public static LocalDateTime beginningOfOffsetDaysConsideringChangeOfDayInLocalDateTime(long offsetDays, Context context) {
        MisPreferencias prefs = new MisPreferencias(context);
        int changeOfDay = prefs.getHourForChangeOfDay();
        return LocalDateTime.now().minusHours(changeOfDay).toLocalDate().atStartOfDay().minusDays(offsetDays).plusHours(changeOfDay);
    }

    public static long endOfOffsetDaysConsideringChangeOfDay(long offsetDays, Context context) {
        LocalDateTime end = endOfOffsetDaysConsideringChangeOfDayInLocalDateTime(offsetDays, context);
        ZonedDateTime zdt = end.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    public static LocalDateTime endOfOffsetDaysConsideringChangeOfDayInLocalDateTime(long offsetDays, Context context) {
        MisPreferencias prefs = new MisPreferencias(context);
        int changeOfDay = prefs.getHourForChangeOfDay();
        return LocalDateTime.now().minusHours(changeOfDay).toLocalDate().atStartOfDay().plusHours(24).minusDays(offsetDays).plusHours(changeOfDay);
    }

    public static int hoursForChangeOfDay(Context context) {
        MisPreferencias prefs = new MisPreferencias(context);
        return prefs.getHourForChangeOfDay();
    }
}
