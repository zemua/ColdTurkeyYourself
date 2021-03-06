package devs.mrp.coolyourturkey.databaseroom.deprecated.conditionnegativetogroup;

import androidx.room.TypeConverter;

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
