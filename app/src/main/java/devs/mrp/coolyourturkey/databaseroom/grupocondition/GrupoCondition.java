package devs.mrp.coolyourturkey.databaseroom.grupocondition;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "grupocondition")
public class GrupoCondition {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "groupid")
    private Integer groupid;

    @NonNull
    @ColumnInfo(name = "conditionalgroupid")
    private Integer conditionalgroupid;

    @NonNull
    @ColumnInfo(name = "conditionalminutes")
    private Integer conditionalminutes;

    @NonNull
    @ColumnInfo(name = "fromlastndays")
    private Integer fromlastndays;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @NonNull
    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(@NonNull Integer groupid) {
        this.groupid = groupid;
    }

    public Integer getConditionalgroupid() {
        return conditionalgroupid;
    }

    public void setConditionalgroupid(Integer conditionalgroupid) {
        this.conditionalgroupid = conditionalgroupid;
    }

    public Integer getConditionalminutes() {
        return conditionalminutes;
    }

    public void setConditionalminutes(Integer conditionalminutes) {
        this.conditionalminutes = conditionalminutes;
    }

    public Integer getFromlastndays() {
        return fromlastndays;
    }

    public void setFromlastndays(Integer fromlastndays) {
        this.fromlastndays = fromlastndays;
    }

    public static GrupoCondition from(GrupoCondition c) {
        GrupoCondition condition = new GrupoCondition();
        //condition.id = c.id; // copy all except the unique id
        condition.groupid = c.groupid;
        condition.conditionalgroupid = c.conditionalgroupid;
        condition.conditionalminutes = c.conditionalminutes;
        condition.fromlastndays = c.fromlastndays;
        return condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrupoCondition that = (GrupoCondition) o;
        return id.equals(that.id) && groupid.equals(that.groupid) && conditionalgroupid.equals(that.conditionalgroupid) && conditionalminutes.equals(that.conditionalminutes) && fromlastndays.equals(that.fromlastndays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, groupid, conditionalgroupid, conditionalminutes, fromlastndays);
    }
}
