package devs.mrp.coolyourturkey.grupos.packagemapper;

import java.util.function.Consumer;

public interface PackageConditionsChecker {
    public void onAllConditionsMet(String packageName, Consumer<Boolean> action);
    public void onAllConditionsMet(String packageName, Consumer<Boolean> action, Consumer<String> message);
}
