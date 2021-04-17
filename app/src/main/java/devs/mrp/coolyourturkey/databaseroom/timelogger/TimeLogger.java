package devs.mrp.coolyourturkey.databaseroom.timelogger;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timelogger")
public class TimeLogger {

    public enum Type {
        POSITIVE, NEGATIVE, NEUTRAL;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "packagename")
    private String packageName;

    @ColumnInfo(name = "groupid")
    private Integer groupId;

    @NonNull
    @ColumnInfo(name = "positivenegative")
    private String positivenegative;

    @NonNull
    @ColumnInfo(name = "usedtime")
    private Long usedtimemilis;

    @NonNull
    @ColumnInfo(name = "countedtime")
    private Long countedtimemilis;

    public void setId(Integer id){this.id = id;}
    public Integer getId(){return this.id;}
    public void setPackageName(String packageName){this.packageName = packageName;}
    public String getPackageName(){return this.packageName;}
    public void setGroupId(Integer groupId){this.groupId = groupId;}
    public Integer getGroupId(){return this.groupId;}
    public void setPositivenegative(Type type){this.positivenegative = type.toString();}
    public Type getPositiveNegative(){return Type.valueOf(positivenegative);}
    public void setUsedtimemilis(Long milis){this.usedtimemilis = milis;}
    public Long getUsedtimemilis(){return this.usedtimemilis;}
    public void setCountedtimemilis(Long milis){this.countedtimemilis = milis;}
    public Long getCountedtimemilis(){return this.countedtimemilis;}

}
