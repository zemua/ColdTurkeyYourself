package devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup;

import androidx.room.TypeConverter;

import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;

public class NegativeConditionToTypeConverter {
    @TypeConverter
    public String fromConditionType(ConditionNegativeToGroup.ConditionType conditionType) {
        return conditionType.toString();
    }
    @TypeConverter
    public ConditionNegativeToGroup.ConditionType toConditionType(String conditionType) {
        return ConditionNegativeToGroup.ConditionType.valueOf(conditionType);
    }
}
