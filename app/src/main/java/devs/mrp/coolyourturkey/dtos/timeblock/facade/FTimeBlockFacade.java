package devs.mrp.coolyourturkey.dtos.timeblock.facade;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

public class FTimeBlockFacade {

    public static ITimeBlockFacade getNew(Application app, LifecycleOwner owner) {
        return new TimeBlockFacade(app, owner);
    }

}
