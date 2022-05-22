package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;
import java.util.Objects;

import devs.mrp.coolyourturkey.databaseroom.conditiontogroup_old_deprecated.ConditionToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;

public class ConditionHelper {

    private static final String TIME_REGEX = "[0-9]*";

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

    public boolean checkEmptyGroup(Spinner groupSpinner) {
        if (groupSpinner.getSelectedItemPosition() == -1) {
            groupSpinner.setBackgroundColor(Color.RED);
            return true;
        }
        groupSpinner.setBackgroundColor(Color.TRANSPARENT);
        return false;
    }

    public void setupEditExistingCondition(GrupoCondition condition, Spinner grupoSpinner, List<GrupoCondition> grupos, EditText hours, EditText minutes, EditText lastDays) {
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

}
