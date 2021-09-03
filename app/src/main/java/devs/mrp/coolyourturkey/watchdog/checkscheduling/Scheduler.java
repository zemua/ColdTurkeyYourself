package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.util.Log;

import java.util.Set;
import java.util.stream.Collectors;

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
    private Set<Integer> daysIncluded;

    @Override
    public boolean outisdeQueryIfOnSchedule(long milis) { // TODO to count the timeBlockDays in the schedule
        now = System.currentTimeMillis();
        currentSchedule = milis;
        return scheduleInsideTimeFrame() && ifDayIncluded(milis);
    }

    @Override
    public Long schedule(AbstractTimeBlock block, Long schedule) {
        daysIncluded = block.getDays().stream().map(d -> d+1).collect(Collectors.toSet()); // sum +1 to equal ZonedTime day-of-week standard
        //Log.d(TAG, "schedule block " + block.getName());
        now = System.currentTimeMillis();
        hourNow = MilisToTime.milisDateToMilisTime(now);
        //Log.d(TAG, "hoursNow: " + hourNow);
        from = block.getFromTime();
        //Log.d(TAG, "from: " + from);
        to = block.getToTime();
        //Log.d(TAG, "to: " + to);
        currentSchedule = schedule;
        //Log.d(TAG, "currentSchedle: " + currentSchedule);
        Log.d(TAG, "block minimum lapse = " + block.getMinimumLapse());
        Log.d(TAG, "block maximum lapse = " + block.getMaximumLapse());
        randomLapse = (long) (Math.random() * (block.getMaximumLapse()-block.getMinimumLapse()))+block.getMinimumLapse();
        //Log.d(TAG, "randomLapse: " + randomLapse);
        if (!scheduleInsideTimeFrame() || !ifDayIncluded(currentSchedule)) {
            //Log.d(TAG, "schedule outside time frame");
            return now + timeToPassFromNow();
        } else {
            //Log.d(TAG, "schedule inside time frame");
            return schedule;
        }
    }

    private long timeFrame() {
        //Log.d(TAG, "time frame...");
        if (from == to) {
            //Log.d(TAG, "from == to so... " + fullday);
            return fullday;
        }
        if (from < to) {
            //Log.d(TAG, "from < to so... " + (to-from));
            return to - from;
        }
        if (from > to) {
            //Log.d(TAG, "from > to so... " + (fullday-from));
            return (fullday - from) + to;
        }
        //Log.d(TAG, "none matches, so... 0");
        return 0;
    }

    private long timeNotOfFrame() {
        //Log.d(TAG, "time outside of frame...");
        if (from == to) {
            //Log.d(TAG, "from == to so... 0");
            return 0L;
        }
        if (from < to) {
            //Log.d(TAG, "from < to so... " + (fullday-to+from));
            return fullday-to+from;
        }
        if (from > to) {
            //Log.d(TAG, "from > to so... " + (from-to));
            return from-to;
        }
        //Log.d(TAG, "none matches so... " + fullday);
        return fullday;
    }

    private boolean nowInsideTimeFrame() {
        //Log.d(TAG, "now inside time frame... " + insideTimeFrame(hourNow));
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
        //Log.d(TAG, "inside time frame?");
        if (from == to) {
            //Log.d(TAG, "inside time frame because from == to");
            return true;
        }
        if (from < to) {
            //Log.d(TAG, "from < to, so... " + (value >= from && value <= to));
            return value >= from && value <= to;
        }
        if (from > to) {
            //Log.d(TAG, "from > to, so... " + (value >= from || value <= to));
            return value >= from || value <= to;
        }
        //Log.d(TAG, "none matches, so... false");
        return false;
    }

    private long milisToJumpFromNow() {
        //Log.d(TAG, "how many frames to pass?");
        long timeJumped = 0;
        long timeToDecrease = randomLapse;
        long nextStart = 0;
        if (nowInsideTimeFrame()) {
            //Log.d(TAG, "now inside time frame, so...");
            if (ifDayIncluded(now)) {
                //Log.d(TAG, "ramdomLapse < to-hourNow so... 0");
                if (timeToDecrease > to-hourNow){
                    //Log.d(TAG, "point A: add +1 jump, decrease to-hourNow, next Start now+fullday-hournow+from");
                    timeJumped += fullday-hourNow+from;
                    timeToDecrease -= to-hourNow;
                    nextStart = now+fullday-hourNow+from;
                } else {
                    //Log.d(TAG, "point B: return 0");
                    return timeToDecrease;
                }
            } else {
                //Log.d(TAG, "1 + " + randomLapse + " - " + to + " + " + hourNow + " / " + timeFrame() + " ... equals = " + (1 + ((randomLapse-to+hourNow)/timeFrame())));
                //Log.d(TAG, "point C: jump+1 and nextStart = now+fullday-hournow+from");
                timeJumped += fullday-hourNow+from;
                nextStart = now+fullday-hourNow+from; // decrease time to tomorrow on open timeframe
            }
        } else {
            if (hourNow <= from) {
                //Log.d(TAG, "point D: nextStart = now+from-hourNow");
                timeJumped += from-hourNow;
                nextStart = now+from-hourNow;
            } else {
                //Log.d(TAG, "point E: nextStart = new+fullday-hournow+from");
                timeJumped += fullday-hourNow+from;
                nextStart = now+fullday-hourNow+from;
            }
        }
        //Log.d(TAG, "" + randomLapse + "/" + timeFrame() + " equals = " + (randomLapse/timeFrame()));
        while (timeToDecrease > timeFrame() || !ifDayIncluded(nextStart)) {
            if (ifDayIncluded(nextStart)){
                timeToDecrease -= timeFrame();
            }
            timeJumped += fullday;
            nextStart += fullday;
        }
        timeJumped += timeToDecrease;
        return timeJumped;
    }

    private long timeToPassFromNow() {
        //Log.d(TAG, "frames to pass from now = " + framesToPassFromNow());
        //Log.d(TAG, "time not of frame = " + timeNotOfFrame());
        /*if (nowInsideTimeFrame() && ifDayIncluded(now)) {
            return randomLapse + milisToJumpFromNow() * timeNotOfFrame();
        } else if (from >= hourNow) {
            return from-hourNow + randomLapse + milisToJumpFromNow() * timeNotOfFrame();
        } else {
            return fullday-hourNow+from + randomLapse + milisToJumpFromNow() * timeNotOfFrame();
        }*/
        return milisToJumpFromNow();
    }

    @Override
    public long getNow() {
        //Log.d(TAG, "now is " + now);
        return now;
    }

    private boolean ifDayIncluded(long milis){
        return daysIncluded.contains(MilisToTime.milisToDayOfWeek(milis));
    }

}
