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

    public static LocalDateTime millisToLocalDateTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return date;
    }

    public static long localDateTimeToMillis(LocalDateTime localDateTime) {
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    public static int milisToDayOfWeek(long milis) {
        ZonedDateTime zone = Instant.ofEpochMilli(milis)
                .atZone(ZoneId.systemDefault());
        return zone.getDayOfWeek().getValue();
    }

    public static String millisToHMS(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    public static long getHours(long millis) {
        return millis/(1000*60*60);
    }

    public static long getMinutes(long millis) {
        return (millis%(1000*60*60))/(1000*60);
    }

    public static long getSeconds(long millis) {
        return (millis%(1000*60))/(1000);
    }

    public static String getFormatedHMS(Long milis){
        Formatter formatter = new Formatter();
        if (milis < 0){
            formatter.format("[ - %02d:%02d:%02d ]", getHours(-milis), getMinutes(-milis), getSeconds(-milis));
        } else {
            formatter.format("%02d:%02d:%02d", getHours(milis), getMinutes(milis), getSeconds(milis));
        }
        return formatter.toString();
    }

    public static long daysFromMillis(long milliseconds) {
        return TimeUnit.MILLISECONDS.toDays(milliseconds);
    }

    public static long millisFromDays(long days) {
        return TimeUnit.DAYS.toMillis(days);
    }

    public static long currentDay() {
        return daysFromMillis(System.currentTimeMillis());
    }

    public static long currentDayInMilis() {
        return millisFromDays(currentDay());
    }

    public static long offsetDay(long nDays) {
        return currentDay()-nDays;
    }

    public static long offsetDayInMillis(long nDays) {
        return millisFromDays(offsetDay(nDays));
    }

    public static long millisToBeginningOfDay(long milliseconds) {
        long days = daysFromMillis(milliseconds);
        return millisFromDays(days);
    }

    public static long millisToEndOfDay(long milliseconds) {
        long days = daysFromMillis(milliseconds);
        return millisFromDays(days) + millisFromDays(1);
    }

    public static long beginningOfOffsetDaysConsideringChangeOfDay(long offsetDays, Context context) {
        MisPreferencias prefs = new MisPreferencias(context);
        int changeOfDay = prefs.getHourForChangeOfDay();
        LocalDateTime start = LocalDateTime.now().minusHours(changeOfDay).toLocalDate().atStartOfDay().minusDays(offsetDays).plusHours(changeOfDay);
        ZonedDateTime zdt = start.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    public static long endOfOffsetDaysConsideringChangeOfDay(long offsetDays, Context context) {
        MisPreferencias prefs = new MisPreferencias(context);
        int changeOfDay = prefs.getHourForChangeOfDay();
        LocalDateTime end = LocalDateTime.now().minusHours(changeOfDay).toLocalDate().atStartOfDay().plusHours(24).minusDays(offsetDays).plusHours(changeOfDay);
        ZonedDateTime zdt = end.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }
}
