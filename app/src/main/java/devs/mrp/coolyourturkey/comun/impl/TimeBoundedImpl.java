package devs.mrp.coolyourturkey.comun.impl;

import devs.mrp.coolyourturkey.comun.TimeBounded;

public class TimeBoundedImpl implements TimeBounded {

    private long lastUpdate = 0L;
    private int minutes;

    public TimeBoundedImpl() {
        this.minutes = 1;
    }

    public TimeBoundedImpl(int minutes) {
        this.minutes = minutes>0 ? minutes : 1;
    }

    @Override
    public boolean isTimeExpired() {
        long now = System.currentTimeMillis();
        long time = minutes*60*1000; // to millis
        if (now >= lastUpdate+time) {
            lastUpdate = now;
            return true;
        }
        return false;
    }
}
