package devs.mrp.coolyourturkey.databaseroom.checktimeblocks;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "checktimeblock")
public class CheckTimeBlock {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "blockid")
    private Integer blockid;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "fromtime")
    private Long fromtime;

    @NonNull
    @ColumnInfo(name = "totime")
    private Long totime;

    @NonNull
    @ColumnInfo(name = "minlapse")
    private Long minlapse;

    @NonNull
    @ColumnInfo(name = "maxlapse")
    private Long maxlapse;

    @NonNull
    @ColumnInfo(name = "prizeammount")
    private Long prizeammount;

    @ColumnInfo(name = "monday")
    private boolean monday;

    @ColumnInfo(name = "tuesday")
    private boolean tuesday;

    @ColumnInfo(name = "wednesday")
    private boolean wednesday;

    @ColumnInfo(name = "thursday")
    private boolean thursday;

    @ColumnInfo(name = "friday")
    private boolean friday;

    @ColumnInfo(name = "saturday")
    private boolean saturday;

    @ColumnInfo(name = "sunday")
    private boolean sunday;

    @NonNull
    public Integer getBlockid() {
        return blockid;
    }

    public void setBlockid(@NonNull Integer blockid) {
        this.blockid = blockid;
    }

    @NonNull
    public Long getFromtime() {
        return fromtime;
    }

    public void setFromtime(@NonNull Long fromtime) {
        this.fromtime = fromtime;
    }

    @NonNull
    public Long getTotime() {
        return totime;
    }

    public void setTotime(@NonNull Long totime) {
        this.totime = totime;
    }

    @NonNull
    public Long getMinlapse() {
        return minlapse;
    }

    public void setMinlapse(@NonNull Long minlapse) {
        this.minlapse = minlapse;
    }

    @NonNull
    public Long getMaxlapse() {
        return maxlapse;
    }

    public void setMaxlapse(@NonNull Long maxlapse) {
        this.maxlapse = maxlapse;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public Long getPrizeammount() {
        return prizeammount;
    }

    public void setPrizeammount(@NonNull Long priceammount) {
        this.prizeammount = priceammount;
    }
}
