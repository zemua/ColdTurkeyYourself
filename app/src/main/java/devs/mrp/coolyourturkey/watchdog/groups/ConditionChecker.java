package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;

import java.util.Map;

import devs.mrp.coolyourturkey.comun.FileReader;

public interface ConditionChecker {

    public Map<Long, FileReader.DayConsumption> getConsumptionByDay(Application app, String fileTarget);

    public long consumptionSinceDays(long lastDays);

    public void addConsumptionByDaysToMap(Map<Integer, Long> map);

}
