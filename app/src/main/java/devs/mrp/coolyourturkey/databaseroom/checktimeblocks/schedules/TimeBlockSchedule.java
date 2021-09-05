package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.schedules;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timeblockschedule")
public class TimeBlockSchedule {

    public TimeBlockSchedule() {

    }

    public TimeBlockSchedule(Integer id, Long milis) {
        this.scheduleid = id;
        this.scheduleMillis = milis;
    }

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "scheduleid")
    private Integer scheduleid;

    @NonNull
    @ColumnInfo(name = "schedulemillis")
    private Long scheduleMillis;

    @NonNull
    public Integer getScheduleid() {
        return scheduleid;
    }

    public void setScheduleid(@NonNull Integer scheduleid) {
        this.scheduleid = scheduleid;
    }

    @NonNull
    public Long getScheduleMillis() {
        return scheduleMillis;
    }

    public void setScheduleMillis(@NonNull Long scheduleMillis) {
        this.scheduleMillis = scheduleMillis;
    }
}
