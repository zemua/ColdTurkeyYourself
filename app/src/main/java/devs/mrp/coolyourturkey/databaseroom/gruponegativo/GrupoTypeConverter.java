package devs.mrp.coolyourturkey.databaseroom.gruponegativo;

import androidx.room.TypeConverter;

public class GrupoTypeConverter {
    @TypeConverter
    public String fromGrupoType(GrupoType grupoType) {
        return grupoType.toString();
    }
    @TypeConverter
    public GrupoType toGrupoType(String grupoType) {
        return GrupoType.valueOf(grupoType);
    }
}
