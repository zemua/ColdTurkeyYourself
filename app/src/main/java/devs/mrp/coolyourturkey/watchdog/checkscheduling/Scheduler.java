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

    public void setTimeBlock(AbstractTimeBlock block) {
        mBlock = block;
        daysIncluded = block.getDays().stream().map(d -> d+1).collect(Collectors.toSet()); // sum +1 to equal ZonedTime day-of-week standard
        now = System.currentTimeMillis();
        hourNow = MilisToTime.milisDateToMilisTime(now);
        from = block.getFromTime();
        to = block.getToTime();
    }

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
        randomLapse = (long) (Math.random() * (block.getMaximumLapse()-block.getMinimumLapse()))+block.getMinimumLapse();
        if (!scheduleInsideTimeFrame() || !ifDayIncluded(currentSchedule) || scheduleLongerThanMax()) {
            Log.d(TAG, "outside of time frame");
            Log.d(TAG, "now: " + now);
            Log.d(TAG, "time to pass from now: " + timeToPassFromNow());
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
        if (isCurrentFrameEnough(timeLeft)) {
            return timeLeft;
        }

        long jumpTime = (framesNeeded(timeLeft)) * fullday;
        jumpTime += timeUntilNextFromPoint();

        long remainingTime = timeRemainingOnIncompletedFrame(timeLeft);
        jumpTime += timeToGoOverForNextFrame(remainingTime);

        while(!ifDayIncluded(now+jumpTime) && daysIncluded.size() > 0){ // if the day of week is not included in the list try next day
            jumpTime += fullday;
        }
        return jumpTime;
    }

    // replaced and simplified in previous function because of bugs
    /*private long milisToJumpFromNow(long timeLeft) {
        Log.d(TAG, "calculate milis to jump for " + mBlock.getName());
        long timeJumped = 0;
        long timeToDecrease = timeLeft;
        long nextStart = 0;
        if (nowInsideTimeFrame()) {
            if (ifDayIncluded(now)) {
                if (to > hourNow){
                    // we are later than "from" and earlier than "to"
                    if (timeToDecrease > to-hourNow){
                        timeJumped += fullday-hourNow+from;
                        timeToDecrease -= to-hourNow;
                        nextStart = now+fullday-hourNow+from;
                        Log.d(TAG, "Cond A");
                    } else {
                        Log.d(TAG, "Cond B");
                        return timeToDecrease;
                    }
                } else {
                    // "to" is earlier than "from" and we are later than "from"
                    // so we have until the end of today to schedule
                    if (timeToDecrease > fullday-hourNow+to) {
                        timeJumped += fullday-hourNow+from;
                        timeToDecrease -= to+fullday-hourNow;
                        nextStart = now+fullday-hourNow+from;
                        Log.d(TAG, "Cond A2");
                    } else {
                        Log.d(TAG, "Cond B2");
                        return timeToDecrease;
                    }
                }
            } else {
                timeJumped += fullday-hourNow+from;
                nextStart = now+fullday-hourNow+from; // decrease time to tomorrow on open timeframe
                Log.d(TAG, "Cond C");
            }
        } else {
            if (hourNow <= from) {
                timeJumped += from-hourNow;
                nextStart = now+from-hourNow;
                Log.d(TAG, "Cond D");
            } else {
                timeJumped += fullday-hourNow+from;
                nextStart = now+fullday-hourNow+from;
                Log.d(TAG, "Cond E");
            }
        }
        while (timeToDecrease > timeFrame() || !ifDayIncluded(nextStart)) {
            Log.d(TAG, "Cond F1");
            if (ifDayIncluded(nextStart)){
                timeToDecrease -= timeFrame();
                Log.d(TAG, "Cond F2");
            }
            timeJumped += fullday;
            nextStart += fullday;
        }
        Log.d(TAG, "done");
        timeJumped += timeToDecrease;
        return timeJumped;
    }*/

    private long timeRemainingInCurrentFrame() {
        if (!insideTimeFrame(hourNow)){
            return 0;
        } else if (hourNow < to) {
            return to-hourNow;
        } else {
            return fullday-hourNow+to;
        }
    }

    private long timePerFrame() {
        if (from == to) {
            return fullday;
        }
        else if (from < to) {
            return to-from;
        }
        else {
            return fullday-from+to;
        }
    }

    private long framesNeeded(long timeLeft) {
        return timeLeft / timePerFrame();
    }

    private long timeRemainingOnIncompletedFrame(long timeLeft) {
        return timeLeft % timePerFrame();
    }

    private boolean isCurrentFrameEnough(long lapse) {
        return timeRemainingInCurrentFrame() >= lapse;
    }

    private long timeUntilNextFromPoint() {
        if (from >= hourNow) {
            return from-hourNow;
        } else {
            return fullday-hourNow+from;
        }
    }

    private long timeToGoOverForNextFrame(long timeLeft) {
        return timeLeft - timeRemainingInCurrentFrame();
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
