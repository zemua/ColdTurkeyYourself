package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import android.util.Log;

import java.util.Set;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public class Scheduler implements IScheduler{

    private final String TAG = "Scheduler";

    private AbstractTimeBlock mBlock;
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
        mBlock = block;
        daysIncluded = block.getDays().stream().map(d -> d+1).collect(Collectors.toSet()); // sum +1 to equal ZonedTime day-of-week standard
        now = System.currentTimeMillis();
        hourNow = MilisToTime.milisDateToMilisTime(now);
        from = block.getFromTime();
        to = block.getToTime();
        currentSchedule = schedule;
        Log.d(TAG, "block minimum lapse = " + block.getMinimumLapse());
        Log.d(TAG, "block maximum lapse = " + block.getMaximumLapse());
        randomLapse = (long) (Math.random() * (block.getMaximumLapse()-block.getMinimumLapse()))+block.getMinimumLapse();
        if (!scheduleInsideTimeFrame() || !ifDayIncluded(currentSchedule) || scheduleLongerThanMax()) {
            return now + timeToPassFromNow();
        } else {
            return schedule;
        }
    }

    private long timeFrame() {
        if (from == to) {
            return fullday;
        }
        if (from < to) {
            return to - from;
        }
        if (from > to) {
            return (fullday - from) + to;
        }
        return 0;
    }

    private long timeNotOfFrame() {
        if (from == to) {
            return 0L;
        }
        if (from < to) {
            return fullday-to+from;
        }
        if (from > to) {
            return from-to;
        }
        return fullday;
    }

    private boolean nowInsideTimeFrame() {
        return insideTimeFrame(hourNow);
    }

    private boolean scheduleInsideTimeFrame() {
        if (currentSchedule < now) {
            return false;
        }
        else {
            return insideTimeFrame(MilisToTime.milisDateToMilisTime(currentSchedule));
        }
    }

    private boolean scheduleLongerThanMax() {
        long maxToJump = milisToJumpFromNow(mBlock.getMaximumLapse());
        if (currentSchedule > now + maxToJump) {
            return true;
        }
        return false;
    }

    private boolean insideTimeFrame(long value) {
        if (from == to) {
            return true;
        }
        if (from < to) {
            return value >= from && value <= to;
        }
        if (from > to) {
            return value >= from || value <= to;
        }
        return false;
    }

    private long milisToJumpFromNow(long timeLeft) {
        long timeJumped = 0;
        long timeToDecrease = timeLeft;
        long nextStart = 0;
        if (nowInsideTimeFrame()) {
            if (ifDayIncluded(now)) {
                if (timeToDecrease > to-hourNow){
                    timeJumped += fullday-hourNow+from;
                    timeToDecrease -= to-hourNow;
                    nextStart = now+fullday-hourNow+from;
                } else {
                    return timeToDecrease;
                }
            } else {
                timeJumped += fullday-hourNow+from;
                nextStart = now+fullday-hourNow+from; // decrease time to tomorrow on open timeframe
            }
        } else {
            if (hourNow <= from) {
                timeJumped += from-hourNow;
                nextStart = now+from-hourNow;
            } else {
                timeJumped += fullday-hourNow+from;
                nextStart = now+fullday-hourNow+from;
            }
        }
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
        return milisToJumpFromNow(randomLapse);
    }

    @Override
    public long getNow() {
        return now;
    }

    private boolean ifDayIncluded(long milis){
        return daysIncluded.contains(MilisToTime.milisToDayOfWeek(milis));
    }

}
