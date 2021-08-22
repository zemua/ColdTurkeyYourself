package devs.mrp.coolyourturkey.condicionesnegativas;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.databaseroom.conditionnegativetogroup.ConditionNegativeToGroup;
import devs.mrp.coolyourturkey.watchdog.groups.TimeLogHandler;

public class NegativeTimeLogHandler {
    private TimeLogHandler timeLogHandler;

    public NegativeTimeLogHandler(Context context, Application application, LifecycleOwner lifecycleOwner) {
        timeLogHandler = new TimeLogHandler(context, application, lifecycleOwner);
    }

    public Long getTimeCountedOnNegativeCondition(ConditionNegativeToGroup condition) {
        // TODO equivalent to public Long getTimeCountedOnGroupCondition(ConditionToGroup cond)
        return null;
    }

    public boolean ifNegativeConditionMet(ConditionNegativeToGroup condition) {
        // TODO equivalent to public boolean ifConditionMet(ConditionToGroup cond)
        return false;
    }

}
