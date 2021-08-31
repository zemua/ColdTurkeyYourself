package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

public class FDbChecksAsSelectable {

    public static INegativeAsSelectable getNegative(Application app, LifecycleOwner owner) {
        return new NegativesAsSelectable(app, owner);
    }

    public static IPositiveAsSelectable getPositive(Application app, LifecycleOwner owner) {
        return new PositivesAsSelectable(app, owner);
    }

}
