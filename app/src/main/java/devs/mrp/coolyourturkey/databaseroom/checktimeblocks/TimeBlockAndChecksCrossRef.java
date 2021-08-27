package devs.mrp.coolyourturkey.databaseroom.checktimeblocks;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"blockid", "id"}, tableName = "timeblockandcheckcrossref")
public class TimeBlockAndChecksCrossRef {
    @NonNull
    public Integer blockid;
    @NonNull
    public Integer id;

    @NonNull
    public Integer getBlockid() {
        return blockid;
    }

    public void setBlockid(@NonNull Integer blockid) {
        this.blockid = blockid;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }
}
