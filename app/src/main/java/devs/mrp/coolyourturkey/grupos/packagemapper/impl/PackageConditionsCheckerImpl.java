package devs.mrp.coolyourturkey.grupos.packagemapper.impl;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionCheckerCommander;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageConditionsChecker;
import devs.mrp.coolyourturkey.grupos.packagemapper.PackageMapper;

public class PackageConditionsCheckerImpl implements PackageConditionsChecker {

    private ConditionCheckerCommander checker;
    private PackageMapper mapper;

    public PackageConditionsCheckerImpl(ConditionCheckerCommander checker, PackageMapper mapper) {
        this.checker = checker;
        this.mapper = mapper;
    }

    @Override
    public void onAllConditionsMet(String packageName, Consumer<Boolean> action) {
        onAllConditionsMet(packageName, action, (s) -> {});
    }

    @Override
    public void onAllConditionsMet(String packageName, Consumer<Boolean> action, Consumer<String> message) {
        mapper.groupIdFromPackageName(packageName, groupId -> {
            if (groupId > 0) {
                checker.onAllConditionsMet(groupId, (isMet) -> action.accept(isMet), (msg) -> message.accept(msg));
            } else {
                // if no assigned to group, then no conditions
                action.accept(true);
            }
        });
    }

}
