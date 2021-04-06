package devs.mrp.coolyourturkey.usagestats;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

public class StatsHandler {

    private static final String TAG = "STATS_HANDLER";

    private Long start;
    private Long end;
    final UsageStatsManager usageStatsManager;
    Context mContext;
    Calendar mHoy;
    Calendar mAyer;
    Calendar mManhana;

    public StatsHandler(Context context) {
        mContext = context;
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        mHoy = Calendar.getInstance();
        mAyer = Calendar.getInstance();
        mAyer.set(Calendar.DATE, -1);
        mManhana = Calendar.getInstance();
        mManhana.set(Calendar.DATE, +1);
    }

    private void setTiemposHoy(){
        start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        end = ZonedDateTime.now().toInstant().toEpochMilli();
        mHoy.setTimeInMillis(end);

        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
        String inicio = formatter.format(start);
        String elfinal = formatter.format(end);
        //Log.d(TAG, "inicio: " + inicio + " final: " + elfinal);
    }

    public ArrayList<UsageStats> getDetailUsageStats() {
        setTiemposHoy();
        ArrayList<UsageStats> lStats = new ArrayList(usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end));
        ListIterator<UsageStats> lIterator = lStats.listIterator();
        while (lIterator.hasNext()) {
            UsageStats lUsageStats = lIterator.next();
            Calendar leste = Calendar.getInstance();
            leste.setTimeInMillis(lUsageStats.getLastTimeStamp());
            int ldiatemp = leste.get(Calendar.DAY_OF_MONTH);
            int lmestemp = leste.get(Calendar.MONTH);
            int lanhotemp = leste.get(Calendar.YEAR);
            int ldiahoy = mHoy.get(Calendar.DAY_OF_MONTH);
            int lmeshoy = mHoy.get(Calendar.MONTH);
            int lanhohoy = mHoy.get(Calendar.YEAR);
            if (ldiatemp != ldiahoy || lmestemp != lmeshoy || lanhotemp != lanhohoy) {
                lIterator.remove();
            } else {
                //Log.d(TAG, "mantenemos " + lUsageStats.getPackageName() + " uso " + lUsageStats.getTotalTimeVisible());
            }
        }
        return lStats;
    }

    public Map<String, UsageStats> getGeneralUsageStats() {
        setTiemposHoy();
        return usageStatsManager.queryAndAggregateUsageStats(start, end);
    }
}
