package devs.mrp.coolyourturkey.databaseroom.elementtogroup;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "elementtogroup")
public class ElementToGroup {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "toid")
    private Long toId;

    @ColumnInfo(name = "type")
    @TypeConverters(ElementTypeConverter.class)
    private ElementType type;

    @ColumnInfo(name = "groupid")
    private Integer groupId;

    public ElementToGroup withName(String s) {
        this.name = s;
        return this;
    }

    public ElementToGroup withToId(Long l) {
        this.toId = l;
        return this;
    }

    public ElementToGroup withType(ElementType e) {
        this.type = e;
        return this;
    }

    public ElementToGroup withGroupId(Integer i) {
        this.groupId = i;
        return this;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
