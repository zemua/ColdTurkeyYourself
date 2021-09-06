package devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;
import devs.mrp.coolyourturkey.databaseroom.conditiontogroup.ConditionToGroup;

@Entity(tableName = "conditionnegativetogroup")
public class ConditionNegativeToGroup {

    public static List<Integer> getTypesList() {
        List<Integer> typesList = new ArrayList<>();
        ConditionToGroup.ConditionType[] mtypes = ConditionToGroup.ConditionType.values();
        for (int i = 0; i<mtypes.length; i++) {
            typesList.add(mtypes[i].getResourceId());
        }
        return typesList;
    }

    public enum ConditionType {
        GROUP(R.string.tiempo_ganado_en_otro_grupo, 0), RANDOMCHECK(R.string.controles_aleatorios, 1), FILE(R.string.tiempo_importado_de_un_archivo, 1);
        private Integer resourceId;
        private Integer position;
        ConditionType(Integer resource, Integer pos) {
            resourceId = resource;
            position = pos;
        }
        public Integer getResourceId(){
            return resourceId;
        }
        public Integer getPosition() { return position; }
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "type")
    @TypeConverters(NegativeConditionToTypeConverter.class)
    private ConditionNegativeToGroup.ConditionType type;

    @ColumnInfo(name = "filetarget")
    private String filetarget;

    @ColumnInfo(name = "conditionalgroupid")
    private Integer conditionalgroupid;

    @ColumnInfo(name = "conditionalblockid")
    private Integer conditionalblockid;

    @ColumnInfo(name = "conditionalminutes")
    private Integer conditionalminutes;

    @ColumnInfo(name = "fromlastndays")
    private Integer fromlastndays;

    public void setId(Integer id) {this.id = id;}
    public Integer getId() {return this.id;}
    public void setType(ConditionNegativeToGroup.ConditionType type){this.type = type;}
    public ConditionNegativeToGroup.ConditionType getType() {return type;}
    public void setFiletarget(String filetarget) {this.filetarget = filetarget;}
    public String getFiletarget(){return this.filetarget;}
    public void setConditionalgroupid(Integer conditionalgroupid){this.conditionalgroupid = conditionalgroupid;}
    public Integer getConditionalgroupid(){return this.conditionalgroupid;}
    public void setConditionalminutes(Integer minutes){this.conditionalminutes = minutes;}
    public Integer getConditionalminutes(){return this.conditionalminutes;}
    public void setFromlastndays(Integer fromlastndays){this.fromlastndays = fromlastndays;}
    public Integer getFromlastndays(){return this.fromlastndays;}

    public Integer getConditionalblockid() {
        return conditionalblockid;
    }

    public void setConditionalblockid(Integer conditionalblockid) {
        this.conditionalblockid = conditionalblockid;
    }

    public void cloneCondition(ConditionNegativeToGroup c) {
        this.id = c.getId();
        this.type = c.getType();
        this.filetarget = c.getFiletarget();
        this.conditionalgroupid = c.getConditionalgroupid();
        this.conditionalminutes = c.getConditionalminutes();
        this.fromlastndays = c.getFromlastndays();
        this.conditionalblockid = c.getConditionalblockid();
    }

}
