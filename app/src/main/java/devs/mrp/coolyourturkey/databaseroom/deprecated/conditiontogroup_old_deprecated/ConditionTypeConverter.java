package devs.mrp.coolyourturkey.databaseroom.deprecated.conditiontogroup_old_deprecated;

import androidx.room.TypeConverter;

public class ConditionTypeConverter {
    @TypeConverter
    public String fromConditionType(ConditionToGroup.ConditionType conditionType) {
        return conditionType.toString();
    }
    @TypeConverter
    public ConditionToGroup.ConditionType toConditionType(String conditionType) {
        return ConditionToGroup.ConditionType.valueOf(conditionType);
    }
}
