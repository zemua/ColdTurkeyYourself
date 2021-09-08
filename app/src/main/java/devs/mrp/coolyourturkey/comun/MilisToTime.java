package devs.mrp.coolyourturkey.comun;

import android.icu.util.Calendar;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

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

    public static int milisToDayOfWeek(long milis) {
        ZonedDateTime zone = Instant.ofEpochMilli(milis)
                .atZone(ZoneId.systemDefault());
        return zone.getDayOfWeek().getValue();
    }
}
