package devs.mrp.coolyourturkey.databaseroom.checktimeblocks;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import devs.mrp.coolyourturkey.databaseroom.randomchecks.RandomCheck;

public class TimeBlockWithChecks {
    @Embedded public CheckTimeBlock timeBlock;
    @Relation(
            parentColumn = "blockid",
            entityColumn = "id",
            associateBy = @Junction(TimeBlockAndChecksCrossRef.class)
    )
    public List<RandomCheck> checks;

    public CheckTimeBlock getTimeBlock() {
        return timeBlock;
    }

    public void setTimeBlock(CheckTimeBlock timeBlock) {
        this.timeBlock = timeBlock;
    }

    public List<RandomCheck> getChecks() {
        return checks;
    }

    public void setChecks(List<RandomCheck> checks) {
        this.checks = checks;
    }


}
