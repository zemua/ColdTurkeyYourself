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
        assemblers = buildAssemblers(type);
        this.owner = owner;
        this.app = app;
    }

    private List<GroupTimeAssembler> buildAssemblers(GroupType type) {
        switch (type) {
            case POSITIVE:
                return Arrays.asList(new AppsTimeAssembler(owner, app), new RandomChecksTimeAssembler(owner, app), new ExternalTimeAssembler(app, owner));
            case NEGATIVE:
                return Arrays.asList(new AppsTimeAssembler(owner, app));
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public void forGroupToday(int groupId) {
        // TODO
        Set<String> counter = new HashSet<>();
        AtomicLong result = new AtomicLong(0);
        assemblers.stream().forEach(a -> {
            BooleanWrap b = new BooleanWrap(false);
            a.forGroupToday(groupId, l -> {

            });
        });
    }

    @Override
    public void forGroupSinceDays(int groupId) {
        // TODO
    }

    private void gatherAndTrigger(long sum, AtomicLong result, AtomicInteger counter) {

    }
}
