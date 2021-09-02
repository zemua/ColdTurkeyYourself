package devs.mrp.coolyourturkey.watchdog.checkscheduling;

import java.util.Random;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.dtos.timeblock.AbstractTimeBlock;

public class Scheduler implements IScheduler{

    private long now;
    private long hourNow;
    private long currentSchedule;
    private long fullday = 24*60*60*1000;
    private long from;
    private long to;
    private long randomLapse;

    @Override
    public Long schedule(AbstractTimeBlock block, Long schedule) {
        now = System.currentTimeMillis();
        hourNow = MilisToTime.milisDateToMilisTime(now);
        from = block.getFromTime();
        to = block.getToTime();
        currentSchedule = schedule;
        randomLapse = (long) (Math.random() * (block.getMaximumLapse()-block.getMinimumLapse()))+block.getMinimumLapse();
        if (!scheduleInsideTimeFrame()) {
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

    private long framesToPassFromNow() {
        if (nowInsideTimeFrame()) {
            if (randomLapse < to-hourNow) {
                return 0;
            } else {
                return 1 + ((randomLapse-to+hourNow)/timeFrame());
            }
        }
        return randomLapse/timeFrame();
    }

    private long timeToPassFromNow() {
        return randomLapse + framesToPassFromNow() * timeNotOfFrame();
    }

}
