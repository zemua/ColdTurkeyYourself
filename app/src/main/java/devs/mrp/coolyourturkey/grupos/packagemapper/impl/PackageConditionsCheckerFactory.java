package devs.mrp.coolyourturkey.grupos.packagemapper.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import devs.mrp.coolyourturkey.grupos.conditionchecker.impl.ConditionCheckerFactory;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageConditionsChecker;

public class PackageConditionsCheckerFactory {
    public static PackageConditionsChecker get(Application app, LifecycleOwner owner) {
        return new PackageConditionsCheckerImpl(ConditionCheckerFactory.getConditionChecker(app, owner), PackageMapperFactory.get(app, owner));
    }
}
