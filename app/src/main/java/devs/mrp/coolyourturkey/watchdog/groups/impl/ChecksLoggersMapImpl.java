package devs.mrp.coolyourturkey.watchdog.groups.impl;

import android.content.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.comun.MilisToTime;
import devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger.TimeBlockLogger;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLoggersMap;

public class ChecksLoggersMapImpl implements TimeLoggersMap<TimeBlockLogger> {

    private Map<Integer, List<TimeBlockLogger>> map = new HashMap<>();
    private Context mContext;

    public ChecksLoggersMapImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void put(int groupId, List<TimeBlockLogger> loggers) {
        map.put(groupId, loggers);
    }

    @Override
    public long get(int groupId, long offsetDays) {
        List<TimeBlockLogger> loggers = map.get(groupId);
        if (Objects.isNull(loggers)) {
            return 0;
        }
        return loggers.stream()
                // filter only those values that are after the given offset day
                .filter(tl -> {
                    LocalDateTime ldt = MilisToTime.millisToLocalDateTime(tl.getEpoch());
                    LocalDateTime ldt2 = MilisToTime.beginningOfOffsetDaysConsideringChangeOfDayInLocalDateTime(offsetDays, mContext);
                    return ldt.isAfter(ldt2);
                })
                // filter only those values that are within the same day
                .filter(tl -> {
                    LocalDateTime ldt = MilisToTime.millisToLocalDateTime(tl.getEpoch());
                    LocalDateTime ldt2 = MilisToTime.endOfOffsetDaysConsideringChangeOfDayInLocalDateTime(offsetDays, mContext);
                    return ldt.isBefore(ldt2);
                })
                .collect(Collectors.summingLong(logger -> logger.getTimecounted()));
    }
}
