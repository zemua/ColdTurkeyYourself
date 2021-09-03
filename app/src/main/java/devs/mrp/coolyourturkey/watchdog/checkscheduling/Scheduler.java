package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.util.Log;

import java.util.Random;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public class Scheduler implements IScheduler{

    private final String TAG = "Scheduler";

    private long now;
    private long hourNow;
    private long currentSchedule;
    private long fullday = 24*60*60*1000;
    private long from;
    private long to;
    private long randomLapse;

    @Override
    public boolean outisdeQueryIfOnSchedule(long milis) { // TODO to count the timeBlockDays in the schedule
        now = System.currentTimeMillis();
        currentSchedule = milis;
        return scheduleInsideTimeFrame();
    }

    @Override
    public Long schedule(AbstractTimeBlock block, Long schedule) {
        Log.d(TAG, "schedule block " + block.getName());
        now = System.currentTimeMillis();
        hourNow = MilisToTime.milisDateToMilisTime(now);
        from = block.getFromTime();
        Log.d(TAG, "from: " + from);
        to = block.getToTime();
        Log.d(TAG, "to: " + to);
        currentSchedule = schedule;
        Log.d(TAG, "currentSchedle: " + currentSchedule);
        randomLapse = (long) (Math.random() * (block.getMaximumLapse()-block.getMinimumLapse()))+block.getMinimumLapse();
        Log.d(TAG, "randomLapse: " + randomLapse);
        if (!scheduleInsideTimeFrame()) {
            Log.d(TAG, "schedule outside time frame");
            return now + timeToPassFromNow();
        } else {
            Log.d(TAG, "schedule inside time frame");
            return schedule;
        }
    }

    private long timeFrame() {
        Log.d(TAG, "time frame...");
        if (from == to) {
            Log.d(TAG, "from == to so... " + fullday);
            return fullday;
        }
        if (from < to) {
            Log.d(TAG, "from < to so... " + (to-from));
            return to - from;
        }
        if (from > to) {
            Log.d(TAG, "from > to so... " + (fullday-from));
            return (fullday - from) + to;
        }
        Log.d(TAG, "none matches, so... 0");
        return 0;
    }

    private long timeNotOfFrame() {
        Log.d(TAG, "time outside of frame...");
        if (from == to) {
            Log.d(TAG, "from == to so... 0");
            return 0L;
        }
        if (from < to) {
            Log.d(TAG, "from < to so... " + (fullday-to+from));
            return fullday-to+from;
        }
        if (from > to) {
            Log.d(TAG, "from > to so... " + (from-to));
            return from-to;
        }
        Log.d(TAG, "none matches so... " + fullday);
        return fullday;
    }

    private boolean nowInsideTimeFrame() {
        Log.d(TAG, "now inside time frame... " + insideTimeFrame(hourNow));
        return insideTimeFrame(hourNow);
    }

    private boolean scheduleInsideTimeFrame() {
        Log.d(TAG, "schedule inside time frame...");
        if (currentSchedule < now) {
            Log.d(TAG, "past schedule, so... false");
            return false;
        }
        else {
            Log.d(TAG, "future schedule... " + insideTimeFrame(MilisToTime.milisDateToMilisTime(currentSchedule)));
            return insideTimeFrame(MilisToTime.milisDateToMilisTime(currentSchedule));
        }
    }

    private boolean insideTimeFrame(long value) {
        Log.d(TAG, "inside time frame?");
        if (from == to) {
            Log.d(TAG, "inside time frame because from == to");
            return true;
        }
        if (from < to) {
            Log.d(TAG, "from < to, so... " + (value >= from && value <= to));
            return value >= from && value <= to;
        }
        if (from > to) {
            Log.d(TAG, "from > to, so... " + (value >= from || value <= to));
            return value >= from || value <= to;
        }
        Log.d(TAG, "none matches, so... false");
        return false;
    }

    private long framesToPassFromNow() {
        Log.d(TAG, "how many frames to pass?");
        if (nowInsideTimeFrame()) {
            Log.d(TAG, "now inside time frame, so...");
            if (randomLapse < to-hourNow) {
                Log.d(TAG, "ramdomLapse < to-hourNow so... 0");
                return 0;
            } else {
                Log.d(TAG, "1 + " + randomLapse + " - " + to + " + " + hourNow + " / " + timeFrame() + " ... equals = " + (1 + ((randomLapse-to+hourNow)/timeFrame())));
                return 1 + ((randomLapse-to+hourNow)/timeFrame());
            }
        }
        Log.d(TAG, "" + randomLapse + "/" + timeFrame() + " equals = " + (randomLapse/timeFrame()));
        return randomLapse/timeFrame();
    }

    private long timeToPassFromNow() {
        Log.d(TAG, "frames to pass from now = " + framesToPassFromNow());
        Log.d(TAG, "time not of frame = " + timeNotOfFrame());
        return randomLapse + framesToPassFromNow() * timeNotOfFrame();
    }

    @Override
    public long getNow() {
        Log.d(TAG, "now is " + now);
        return now;
    }

}
