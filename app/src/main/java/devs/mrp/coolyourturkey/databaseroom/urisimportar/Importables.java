package devs.mrp.coolyourturkey.databaseroom.urisimportar;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "importables")
public class Importables {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uri")
    private String uri;

    public Importables(String uri){
        this.uri = uri;
    }

    public String getUri(){
        return uri;
    }
}
