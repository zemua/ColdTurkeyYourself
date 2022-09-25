package devs.mrp.coolyourturkey.grupos.conditionchecker.impl;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.FileTimeGetter;
import devs.mrp.coolyourturkey.comun.impl.FileTimeGetterImpl;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroup;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.grupo.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.databaseroom.grupo.grupocondition.GrupoCondition;
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
        LiveData<List<ElementToGroup>> els = elementToGroupRepository.findElementsOfGroupAndType(condition.getConditionalgroupid(), ElementType.FILE);
        els.observe(owner, elements -> {
            els.removeObservers(owner);
            // the files are expected to have the time per day considering hour for change of day in the device that creates them
            long result = elements.stream().mapToLong(e -> fileTimeGetter.fromFileLastDays(condition.getFromlastndays(), Uri.parse(e.getName()))).sum();
            action.accept(result);
        });
    }
}
