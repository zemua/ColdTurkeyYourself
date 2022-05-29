package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.FileTimeGetter;
import devs.mrp.coolyourturkey.comun.impl.FileTimeGetterImpl;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.databaseroom.grupocondition.GrupoCondition;
import devs.mrp.coolyourturkey.grupos.conditionchecker.ConditionChecker;

public class FileChecker implements ConditionChecker {

    private FileTimeGetter fileTimeGetter;
    private ElementToGroupRepository elementToGroupRepository;
    private LifecycleOwner owner;

    public FileChecker(Application app, LifecycleOwner owner) {
        fileTimeGetter = new FileTimeGetterImpl(app);
        elementToGroupRepository = ElementToGroupRepository.getRepo(app);
        this.owner = owner;
    }

    @Override
    public void onTimeCounted(GrupoCondition condition, Consumer<Long> action) {
        elementToGroupRepository.findElementsOfGroupAndType(condition.getConditionalgroupid(), ElementType.FILE)
                .observe(owner, elements -> {
                    long result = elements.stream().mapToLong(e -> fileTimeGetter.fromFileLastDays(condition.getFromlastndays(), Uri.parse(e.getName()))).sum();
                    action.accept(result);
                });
    }
}
