package devs.mrp.coolyourturkey.databaseroom.deprecated.grouplimit;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grouplimit")
public class GroupLimit {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "groupid")
    private Integer groupId;

    @NonNull
    @ColumnInfo(name = "offsetdays")
    private Integer offsetDays;

    @NonNull
    @ColumnInfo(name = "minuteslimit")
    private Integer minutesLimit;

    @NonNull
    @ColumnInfo(name = "blocking")
    private Boolean blocking;

    @NonNull
    @ColumnInfo(name = "solosicondiciones")
    private Boolean solosicondiciones;

    public GroupLimit(Integer groupId, Integer offsetDays, Integer minutesLimit, Boolean blocking, Boolean solosicondiciones) {
        this.groupId = groupId;
        this.offsetDays = offsetDays;
        this.minutesLimit = minutesLimit;
        this.blocking = blocking;
        this.solosicondiciones = solosicondiciones;
    }

    public Integer getId() {return this.id;}
    public void setId(Integer id) {this.id = id;}

    public Integer getGroupId() {return this.groupId;}
    public void setGroupId(Integer id) {this.groupId = id;}

    public Integer getOffsetDays() {return this.offsetDays;}
    public void setOffsetDays(Integer offset) {this.offsetDays = offset;}

    public Integer getMinutesLimit() { return this.minutesLimit; }
    public void setMinutesLimit(Integer limit) {this.minutesLimit = limit;}

    public Boolean getBlocking() { if (blocking != null && blocking == true){return true;} else{return false;} }
    public void setBlocking(Boolean b) {this.blocking=b;}

    public Boolean getSolosicondiciones() {if(solosicondiciones!=null && solosicondiciones==true){return true;} else {return false;}}
    public void setSolosicondiciones(Boolean s) {this.solosicondiciones = s;}

}
