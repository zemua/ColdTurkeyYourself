package devs.mrp.coolyourturkey.databaseroom.grupo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Objects;

@Entity(tableName = "grupo")
public class Grupo {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "nombre")
    private String nombre;

    @NonNull
    @ColumnInfo(name = "type")
    @TypeConverters(GrupoTypeConverter.class)
    private GrupoType type;

    @ColumnInfo(name = "preventclose", defaultValue = "0")
    private boolean preventclose;

    @ColumnInfo(name = "ignoreconditions", defaultValue = "0")
    private boolean ignoreBasedConditions;

    public Grupo(String nombre) {this.nombre = nombre;}

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    @NonNull
    public GrupoType getType() {
        return type;
    }

    public void setType(@NonNull GrupoType type) {
        this.type = type;
    }

    public boolean isPreventclose() {
        if (!GrupoType.NEGATIVE.equals(this.type)) {
            // if not negative, prevent closing always
            return true;
        }
        return Objects.isNull(this.preventclose) ? false : this.preventclose;
    }

    public void setPreventclose(boolean preventclose) {
        this.preventclose = preventclose;
    }

    public boolean isIgnoreBasedConditions() {
        if (!GrupoType.POSITIVE.equals(this.type)) {
            // if not positive, there are no conditions based on it
            return true;
        }
        return Objects.isNull(this.ignoreBasedConditions) ? false : this.ignoreBasedConditions;
    }

    public void setIgnoreBasedConditions(boolean ignoreBasedConditions) {
        this.ignoreBasedConditions = ignoreBasedConditions;
    }
}
