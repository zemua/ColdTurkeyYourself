package devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup;

import androidx.room.TypeConverter;

public class ElementTypeConverter {
    @TypeConverter
    public String fromElementType(ElementType elementType) {return elementType.toString();}
    @TypeConverter
    public ElementType toElementType(String elementType) {return ElementType.valueOf(elementType);}
}
