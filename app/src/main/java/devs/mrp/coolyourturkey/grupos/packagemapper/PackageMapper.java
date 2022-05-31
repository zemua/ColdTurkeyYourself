package devs.mrp.coolyourturkey.grupos.packagemapper;

import java.util.function.Consumer;

public interface PackageMapper {
    public void groupIdFromPackageName(String packageName, Consumer<Integer> groupId);
}
