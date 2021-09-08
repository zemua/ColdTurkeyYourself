package devs.mrp.coolyourturkey.databaseroom.checktimeblocks.export;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timeblockexport")
public class TimeBlockExport {

    public TimeBlockExport(){}

    public TimeBlockExport(Integer blockid, String archivo, Integer days) {
        this.blockid = blockid;
        this.archivo = archivo;
        this.days = days;
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "expblockid")
    private Integer blockid;

    @NonNull
    @ColumnInfo(name = "archivo")
    private String archivo;

    @NonNull
    @ColumnInfo(name = "days")
    private Integer days;

    @NonNull
    public Integer getBlockid() {
        return blockid;
    }

    public void setBlockid(@NonNull Integer blockid) {
        this.blockid = blockid;
    }

    @NonNull
    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(@NonNull String archivo) {
        this.archivo = archivo;
    }

    @NonNull
    public Integer getDays() {
        return days;
    }

    public void setDays(@NonNull Integer days) {
        this.days = days;
    }
}
