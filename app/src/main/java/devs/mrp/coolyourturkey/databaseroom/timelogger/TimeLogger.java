package devs.mrp.coolyourturkey.databaseroom.timelogger;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "timelogger")
public class TimeLogger {

    public enum Type {
        POSITIVE, NEGATIVE, NEUTRAL;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Long id;

    @NonNull
    @ColumnInfo(name = "millistimestamp")
    private Long millistimestamp;

    @NonNull
    @ColumnInfo(name = "packagename")
    private String packageName;

    @ColumnInfo(name = "groupid")
    private Integer groupId;

    @NonNull
    @ColumnInfo(name = "positivenegative")
    @TypeConverters(TimeLoggerTypeConverter.class)
    private Type positivenegative;

    @NonNull
    @ColumnInfo(name = "usedtime")
    private Long usedtimemilis;

    @NonNull
    @ColumnInfo(name = "countedtime")
    private Long countedtimemilis;

    public void setId(Long id){this.id = id;}
    public Long getId(){return this.id;}
    public void setMillistimestamp(Long millisTimestamp){this.millistimestamp = millisTimestamp;}
    public Long getMillistimestamp(){return this.millistimestamp;}
    public void setPackageName(String packageName){this.packageName = packageName;}
    public String getPackageName(){return this.packageName;}
    public void setGroupId(Integer groupId){this.groupId = groupId;}
    public Integer getGroupId(){return this.groupId;}
    public void setPositivenegative(Type type){this.positivenegative = type;}
    public Type getPositivenegative(){return this.positivenegative;}
    public void setUsedtimemilis(Long milis){this.usedtimemilis = milis;}
    public Long getUsedtimemilis(){return this.usedtimemilis;}
    public void setCountedtimemilis(Long milis){this.countedtimemilis = milis;}
    public Long getCountedtimemilis(){return this.countedtimemilis;}

}
