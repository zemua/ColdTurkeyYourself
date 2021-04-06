package devs.mrp.coolyourturkey.databaseroom.valuemap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "value_map")
public class ValueMap {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "valor")
    private String valor;

    public static final String VALOR_TRUE = "true";
    public static final String VALOR_FALSE = "false";

    public ValueMap(@NonNull String nombre, String valor){
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre(){
        return nombre;
    }

    public String getValor(){
        return valor;
    }
}
