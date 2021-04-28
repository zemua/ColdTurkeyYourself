package devs.mrp.coolyourturkey.databaseroom.grupoexport;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grupoexport")
public class GrupoExport {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "groupid")
    private Integer groupId;

    @NonNull
    @ColumnInfo(name = "archivo")
    private String archivo;

    public GrupoExport(Integer groupId, String archivo) {
        this.groupId = groupId;
        this.archivo = archivo;
    }

    public Integer getGroupId() {return this.groupId;}
    public void setGroupId(Integer groupId){this.groupId = groupId;}

    public String getArchivo() {return this.archivo;}
    public void setArchivo(String archivo) {this.archivo = archivo;}

}
