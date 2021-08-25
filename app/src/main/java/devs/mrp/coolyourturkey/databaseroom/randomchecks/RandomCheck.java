package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "randomcheck")
public class RandomCheck {

    public enum CheckType {
        POSITIVE, NEGATIVE;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Integer id;

    @NonNull
    @ColumnInfo(name = "type")
    @TypeConverters(CheckTypeConverter.class)
    private RandomCheck.CheckType type;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "question")
    private String question;

    @ColumnInfo(name = "multiplicador")
    private Integer multiplicador;


    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @NonNull
    public CheckType getType() {
        return type;
    }

    public void setType(@NonNull CheckType type) {
        this.type = type;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getQuestion() {
        return question;
    }

    public void setQuestion(@NonNull String question) {
        this.question = question;
    }

    public Integer getMultiplicador() {
        return multiplicador;
    }

    public void setMultiplicador(Integer multiplicador) {
        this.multiplicador = multiplicador;
    }
}
