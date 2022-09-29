package devs.mrp.coolyourturkey.grupos.timing.impl;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import devs.mrp.coolyourturkey.comun.BooleanWrap;
import devs.mrp.coolyourturkey.grupos.GroupType;
import devs.mrp.coolyourturkey.grupos.timing.GroupGeneralAssembler;
import devs.mrp.coolyourturkey.grupos.timing.GroupTimeAssembler;

public class GeneralAssemblerImpl implements GroupGeneralAssembler {

    private List<GroupTimeAssembler> assemblers;
    private LifecycleOwner owner;
    private Application app;

    public GeneralAssemblerImpl(GroupType type, LifecycleOwner owner, Application app) {
        this.owner = owner;
        this.app = app;
        assemblers = buildAssemblers(type);
    }

    private List<GroupTimeAssembler> buildAssemblers(GroupType type) {
        switch (type) {
            case POSITIVE:
                return Arrays.asList(new AppsTimeAssembler(owner, app), new RandomChecksTimeAssembler(owner, app), new ExternalTimeAssembler(app, owner));
            case NEGATIVE:
                return Arrays.asList(new AppsTimeAssembler(owner, app), new RandomChecksTimeAssembler(owner, app), new ExternalTimeAssembler(app, owner));
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void forGroupToday(int groupId, Consumer<Long> action) {
        forGroupSinceDays(groupId, 0, action);
    }

    @Override
    public void forGroupSinceDays(int groupId, int days, Consumer<Long> action) {
        Set<String> counter = new HashSet<>();
        AtomicLong result = new AtomicLong(0);
        assemblers.stream().forEach(a -> {
            String assemblerType = a.getClass().getSimpleName();
            a.forGroupSinceDays(groupId, days, longValue -> {
                if (counter.contains(assemblerType)){
                    return;
                }
                counter.add(assemblerType);
                Long longResult = result.addAndGet(longValue);
                if (assemblers.size() >= counter.size()) {
                    action.accept(longResult);
                }
            });
        });
    }
}
