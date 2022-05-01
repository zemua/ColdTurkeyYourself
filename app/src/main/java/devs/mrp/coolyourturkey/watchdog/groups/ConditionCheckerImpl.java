package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;
import android.net.Uri;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.comun.FileReader;

public class ConditionCheckerImpl implements ConditionChecker {

    Map<Long, FileReader.DayConsumption> map;

    @Override
    public Map<Long, FileReader.DayConsumption> getConsumptionByDay(Application app, String fileTarget) {
        map = FileReader.readPastDaysConsumption(app, Uri.parse(fileTarget));
        return map;
    }

    @Override
    public long consumptionSinceDays(long lastDays) {
        if (map == null) {
            return 0;
        }
        return map.entrySet().stream()
                .filter(e -> e.getKey().intValue()<lastDays)
                .map(Map.Entry::getValue)
                .map(FileReader.DayConsumption::getConsumption)
                .collect(Collectors.summingLong(Long::longValue));
    }

    @Override
    public void addConsumptionByDaysToMap(Map<Integer, Long> externalMap) {
        externalMap.putAll(map.keySet().stream().collect(Collectors.toMap(key -> key.intValue(), key -> Long.valueOf(consumptionSinceDays(key)))));
    }

}
