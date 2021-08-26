package devs.mrp.coolyourturkey.databaseroom.checktimeblocks;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"blockid", "id"}, tableName = "timeblockandcheckcrossref")
public class TimeBlockAndChecksCrossRef {
    @NonNull
    public Integer blockid;
    @NonNull
    public Integer id;
}
