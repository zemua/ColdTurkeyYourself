package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;
import android.net.Uri;

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
        if (map == null || !map.containsKey(lastDays)) {
            return 0;
        }
        return map.get(lastDays).getConsumption();
    }

    @Override
    public void addConsumptionByDaysToMap(Map<Integer, Long> externalMap) {
        externalMap.putAll(map.entrySet().stream().collect(Collectors.toMap(e -> Integer.valueOf(e.getKey().intValue()), e -> e.getValue().getConsumption())));
    }

}
