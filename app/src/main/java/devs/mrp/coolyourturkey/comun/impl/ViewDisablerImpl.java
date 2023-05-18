package devs.mrp.coolyourturkey.comun.impl;

import android.view.View;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import devs.mrp.coolyourturkey.comun.ViewDisabler;

public class ViewDisablerImpl implements ViewDisabler {

    private List<Map.Entry<List<Supplier<Boolean>>,View>> evaluableViews = new LinkedList<>();

    @Override
    public void addViewConditions(View view, List<Supplier<Boolean>> conditions) {
        Map.Entry<List<Supplier<Boolean>>,View> entry = new AbstractMap.SimpleEntry<>(conditions, view);
        evaluableViews.add(entry);
    }

    @Override
    public void evaluateConditions() {
        evaluableViews.forEach(entry -> evaluateSingleCondition(entry.getValue(), entry.getKey()));
    }

    private void evaluateSingleCondition(View view, List<Supplier<Boolean>> conditions) {
        boolean allMet = conditions.stream().map(Supplier::get).allMatch(Predicate.isEqual(true));
        view.setEnabled(allMet);
    }
}
