package devs.mrp.coolyourturkey.databaseroom.contador;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contador")
public class Contador {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "day_of_epoch")
    private Long dayOfEpoch;

    @ColumnInfo(name = "acumulado")
    private Long acumulado;

    public Contador(Long dayOfEpoch, Long acumulado){
        this.dayOfEpoch = dayOfEpoch;
        this.acumulado = acumulado;
    }

    public Long getDayOfEpoch(){
        return dayOfEpoch;
    }

    public Long getAcumulado(){
        return acumulado;
    }
}
