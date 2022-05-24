package devs.mrp.coolyourturkey.grupos.timing.impl;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;

import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.FileTimeGetter;
import devs.mrp.coolyourturkey.comun.impl.FileTimeGetterImpl;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementToGroupRepository;
import devs.mrp.coolyourturkey.databaseroom.elementtogroup.ElementType;
import devs.mrp.coolyourturkey.grupos.timing.GroupTimeAssembler;

public class ExternalTimeAssembler implements GroupTimeAssembler {

    private FileTimeGetter timer;
    private ElementToGroupRepository repo;
    private LifecycleOwner owner;

    public ExternalTimeAssembler(Application app, LifecycleOwner owner) {
        timer = new FileTimeGetterImpl(app);
        repo = ElementToGroupRepository.getRepo(app);
        this.owner = owner;
    }

    @Override
    public void forGroupToday(int groupId, Consumer<Long> action) {
        forGroupSinceDays(groupId, 0, action);
    }

    @Override
    public void forGroupSinceDays(int groupId, int sinceDays, Consumer<Long> action) {
        repo.findElementsOfGroupAndType(groupId, ElementType.FILE).observe(owner, elements -> {
            long result = elements.stream().mapToLong(e -> timer.fromFileLastDays(sinceDays, Uri.parse(e.getName()))).sum();
            action.accept(result);
        });
    }
}
