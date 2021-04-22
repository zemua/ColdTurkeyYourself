package devs.mrp.coolyourturkey.comun;

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
}
