package devs.mrp.coolyourturkey.databaseroom.conditiontogroup;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

import devs.mrp.coolyourturkey.R;

@Entity(tableName = "conditiontogroup")
public class ConditionToGroup {

    public static List<Integer> getTypesList() {
        List<Integer> typesList = new ArrayList<>();
        ConditionType[] mtypes = ConditionType.values();
        for (int i = 0; i<mtypes.length; i++) {
            typesList.add(mtypes[i].getResourceId());
        }
        return typesList;
    }

    public enum ConditionType {
        GROUP(R.string.tiempo_ganado_en_otro_grupo), FILE(R.string.tiempo_importado_de_un_archivo);
        private Integer resourceId;
        ConditionType(Integer resource) {
            resourceId = resource;
        }
        public Integer getResourceId(){
            return resourceId;
        }
    }


    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "groupid")
    private Integer groupid;

    @NonNull
    @ColumnInfo(name = "type")
    private String type; // we use the name of the ENUM in the db

    @ColumnInfo(name = "filetarget")
    private String filetarget;

    @ColumnInfo(name = "conditionalgroupid")
    private Integer conditionalgroupid;

    @ColumnInfo(name = "conditionalminutes")
    private Integer conditionalminutes;

    @ColumnInfo(name = "fromlastndays")
    private Integer fromlastndays;

    public void setId(Integer id) {this.id = id;}
    public Integer getId() {return this.id;}
    public void setGroupid(Integer groupId) {this.groupid = groupId;}
    public Integer getGroupid() {return this.groupid;}
    public void setType(String type) {this.type = type;}
    public String getType() {return this.type;}
    public void setFiletarget(String filetarget) {this.filetarget = filetarget;}
    public String getFiletarget(){return this.filetarget;}
    public void setConditionalgroupid(Integer conditionalgroupid){this.conditionalgroupid = conditionalgroupid;}
    public Integer getConditionalgroupid(){return this.conditionalgroupid;}
    public void setConditionalminutes(Integer minutes){this.conditionalminutes = minutes;}
    public Integer getConditionalminutes(){return this.conditionalminutes;}
    public void setFromlastndays(Integer fromlastndays){this.fromlastndays = fromlastndays;}
    public Integer getFromlastndays(){return this.fromlastndays;}

}
