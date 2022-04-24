package devs.mrp.coolyourturkey.watchdog.groups;

import android.app.Application;

import java.util.Map;

import devs.mrp.coolyourturkey.comun.FileReader;
import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;

public interface ConditionChecker {

    public Map<Long, FileReader.DayConsumption> getConsumptionByDay(Application app, String fileTarget);

    public long consumptionSinceDays(long lastDays);

    public void addConsumptionByDaysToMap(Map<Integer, Long> map);

}
