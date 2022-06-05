package devs.mrp.coolyourturkey.grupos.packagemapper;

import java.util.function.Consumer;

public interface PackageGroupMapper {
    public void groupNameFromPackageName(String packageName, Consumer<String> groupName);
}
