package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;
import java.util.Objects;

import devs.mrp.coolyourturkey.databaseroom.grupo.Grupo;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;

public class ConditionBuildHelper {

    private static final String TIME_REGEX = "[0-9]*";
    private static final String TIME_REGEX_NON_EMPTY = "[0-9]+";

    public boolean verifyData(EditText minutes, EditText hours, EditText elapsed, Spinner groups) {
        return verifyConditionalTime(minutes, hours) &&
                verifyFromLastNdays(elapsed) &&
                checkCorrectGroup(groups);
    }

    public boolean verifyConditionalTime(EditText minutes, EditText hours) {
        String minutesContent = minutes.getText().toString();
        String hoursContent = hours.getText().toString();
        if (hoursContent.matches(TIME_REGEX)
                && minutesContent.matches(TIME_REGEX)
                && !(ifZero(hoursContent) && ifZero(minutesContent))) {
            hours.setBackgroundColor(Color.TRANSPARENT);
            minutes.setBackgroundColor(Color.TRANSPARENT);
            return true;
        } else {
            hours.setBackgroundColor(Color.RED);
            minutes.setBackgroundColor(Color.RED);
            return false;
        }
    }

    public boolean verifyFromLastNdays(EditText elapsed) {
        String nDays = elapsed.getText().toString();
        if (nDays.matches(TIME_REGEX_NON_EMPTY)){
            elapsed.setBackgroundColor(Color.TRANSPARENT);
            return true;
        } else {
            elapsed.setBackgroundColor(Color.RED);
            return false;
        }
    }

    private boolean ifZero(String number) {
        if ("".equals(number)){
            return true;
        } if (!number.matches(TIME_REGEX)) {
            return true;
        } if (Integer.parseInt(number) == 0) {
            return true;
        }
        return false;
    }

    public boolean checkCorrectGroup(Spinner groupSpinner) {
        if (groupSpinner.getSelectedItemPosition() == -1) {
            groupSpinner.setBackgroundColor(Color.RED);
            return false;
        }
        groupSpinner.setBackgroundColor(Color.TRANSPARENT);
        return true;
    }

    public void setupEditExistingCondition(GrupoCondition condition, Spinner grupoSpinner, List<Grupo> grupos, EditText hours, EditText minutes, EditText lastDays) {
        if (Objects.isNull(condition) || Objects.isNull(grupoSpinner)) {
            return;
        }
        for (int i=0; i<grupos.size(); i++) {
            if (grupos.get(i).getId() == condition.getConditionalgroupid()) {
                grupoSpinner.setSelection(i);
                break;
            }
        }
        hours.setText(String.valueOf(condition.getConditionalminutes()/60));
        minutes.setText(String.valueOf(condition.getConditionalminutes()%60));
        lastDays.setText(String.valueOf(condition.getFromlastndays()));
    }

    public int getConditionalMinutes(EditText hours, EditText minutes) {
        Integer ltime = 0;
        if(!"".equals(hours.getText().toString())){
            ltime += (Integer.parseInt(hours.getText().toString())*60);
        }
        if (!"".equals(minutes.getText().toString())){
            ltime += Integer.parseInt(minutes.getText().toString());
        }
        return ltime;
    }

}
