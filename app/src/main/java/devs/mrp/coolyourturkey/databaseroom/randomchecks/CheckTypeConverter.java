package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import androidx.room.TypeConverter;

public class CheckTypeConverter {
    @TypeConverter
    public String fromCheckType(RandomCheck.CheckType checkType) {
        return checkType.toString();
    }
    @TypeConverter
    public RandomCheck.CheckType toCheckType(String checkType) {
        return RandomCheck.CheckType.valueOf(checkType);
    }
}
