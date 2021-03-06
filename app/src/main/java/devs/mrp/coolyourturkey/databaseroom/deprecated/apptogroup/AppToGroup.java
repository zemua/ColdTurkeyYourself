package devs.mrp.coolyourturkey.databaseroom.deprecated.apptogroup;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "apptogroup")
public class AppToGroup {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "appname")
    private String appName;

    @ColumnInfo(name = "groupid")
    private Integer groupId;

    public AppToGroup(String appName, Integer groupId) {
        this.appName = appName;
        this.groupId = groupId;
    }

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id = id;
    }

    public String getAppName(){
        return appName;
    }
    public void setAppName(String name){
        this.appName = name;
    }

    public Integer getGroupId(){
        return this.groupId;
    }
    public void setGroupId(Integer groupid){
        this.groupId = groupid;
    }
}
