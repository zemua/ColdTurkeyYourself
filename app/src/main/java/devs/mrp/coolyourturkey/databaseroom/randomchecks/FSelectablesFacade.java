package devs.mrp.coolyourturkey.databaseroom.randomchecks;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

public class FSelectablesFacade {

    public static ASelectablesFacade get(Application app, LifecycleOwner owner) {
        return new SelectableFacade(app, owner);
    }

}
