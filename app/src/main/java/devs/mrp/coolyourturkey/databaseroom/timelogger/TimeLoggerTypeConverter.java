package devs.mrp.coolyourturkey.databaseroom.timelogger;

import androidx.room.TypeConverter;

public class TimeLoggerTypeConverter {
    @TypeConverter
    public String fromType(TimeLogger.Type type) {
        return type.toString();
    }
    @TypeConverter
    public TimeLogger.Type toType(String typeString) {
        return TimeLogger.Type.valueOf(typeString);
    }
}
