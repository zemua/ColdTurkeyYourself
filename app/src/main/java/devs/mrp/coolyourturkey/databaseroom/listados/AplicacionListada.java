package devs.mrp.coolyourturkey.databaseroom.listados;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "apps_listadas")
public class AplicacionListada {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "lista")
    private String lista;

    public static final String POSITIVA = "lista positiva";
    public static final String NEGATIVA = "lista negativa";
    public static final String NEUTRAL = "ninguna lista";

    public enum TiposListas {
        positiva(POSITIVA), negativa(NEGATIVA), ninguna(NEUTRAL);

        private String mTipo;
        private TiposListas(String tipo) {
            mTipo = tipo;
        }

        public String getString(){
            return mTipo;
        }
    }

    public AplicacionListada(@NonNull String nombre, String lista) {
        this.nombre = nombre;
        this.lista = lista;
    }

    public static void setStrings(String positivo, String negativo, String neutral){
        //POSITIVA = positivo;
        //NEGATIVA = negativo;
        //NEUTRAL = neutral;
    }

    public static String getPOSITIVA(){
        return POSITIVA;
    }

    public static String getNEGATIVA(){
        return NEGATIVA;
    }

    public static String getNEUTRAL(){
        return NEUTRAL;
    }

    public String getNombre() {
        return nombre;
    }

    public String getLista() {
        return lista;
    }
}
