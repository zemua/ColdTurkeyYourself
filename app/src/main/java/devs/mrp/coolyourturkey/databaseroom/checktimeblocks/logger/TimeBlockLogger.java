package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.logger;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timeblocklogger")
public class TimeBlockLogger {

    public TimeBlockLogger(){}

    public TimeBlockLogger(Integer blockId, Long timeCounted) {
        this.blockid = blockId;
        this.timecounted = timeCounted;
        this.epoch = System.currentTimeMillis();
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "loggerid")
    private Long loggerid;

    @NonNull
    @ColumnInfo(name = "epoch")
    private Long epoch;

    @NonNull
    @ColumnInfo(name = "blockid")
    private Integer blockid;

    @NonNull
    @ColumnInfo(name = "timecounted")
    private Long timecounted;

    @NonNull
    public Long getLoggerid() {
        return loggerid;
    }

    public void setLoggerid(@NonNull Long loggerid) {
        this.loggerid = loggerid;
    }

    @NonNull
    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(@NonNull Long epoch) {
        this.epoch = epoch;
    }

    @NonNull
    public Integer getBlockid() {
        return blockid;
    }

    public void setBlockid(@NonNull Integer blockid) {
        this.blockid = blockid;
    }

    @NonNull
    public Long getTimecounted() {
        return timecounted;
    }

    public void setTimecounted(@NonNull Long timecounted) {
        this.timecounted = timecounted;
    }
}
