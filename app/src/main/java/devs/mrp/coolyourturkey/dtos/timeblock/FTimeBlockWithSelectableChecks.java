package devs.mrp.coolyourturkey.dtos.timeblock;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import devs.mrp.coolyourturkey.dtos.randomcheck.ANegativeCheckSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.APositiveCheckSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.Check;
import devs.mrp.coolyourturkey.dtos.randomcheck.NegativeCheck;
import devs.mrp.coolyourturkey.dtos.randomcheck.NegativeCheckSelectable;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheck;
import devs.mrp.coolyourturkey.dtos.randomcheck.PositiveCheckSelectable;

public class FTimeBlockWithSelectableChecks {

    public APositiveCheckSelectable positiveSelectableFrom(PositiveCheck check, boolean selected){
        APositiveCheckSelectable selectable = new PositiveCheckSelectable();
        selectable.setSelected(selected);
        selectable.setId(check.getId());
        selectable.setMultiplicador(check.getMultiplicador());
        selectable.setName(check.getName());
        selectable.setQuestion(check.getQuestion());
        return selectable;
    }

    public ANegativeCheckSelectable negativeSelectableFrom(Check check, boolean selected) {
        ANegativeCheckSelectable selectable = new NegativeCheckSelectable();
        selectable.setSelected(selected);
        selectable.setId(check.getId());
        selectable.setName(check.getName());
        selectable.setQuestion(check.getQuestion());
        return selectable;
    }

    public List<APositiveCheckSelectable> positiveSelectablesFrom(List<PositiveCheck> allchecks, List<PositiveCheck> selected) {
        Set<Integer> selMap = selected.stream().map(c -> c.getId()).collect(Collectors.toSet());
        return allchecks.stream().map(c -> positiveSelectableFrom(c, selMap.contains(c.getId()))).collect(Collectors.toList());
    }

    public List<ANegativeCheckSelectable> negativeSelectablesFrom(List<Check> allNegChecks, List<Check> selected) {
        Set<Integer> selSet = selected.stream().map(c -> c.getId()).collect(Collectors.toSet());
        return allNegChecks.stream().map(c -> negativeSelectableFrom(c, selSet.contains(c.getId()))).collect(Collectors.toList());
    }

    public List<APositiveCheckSelectable> positiveSelectablesFrom(List<PositiveCheck> allchecks) {
        return allchecks.stream().map(c -> positiveSelectableFrom(c, false)).collect(Collectors.toList());
    }

    public List<ANegativeCheckSelectable> negativeSelectablesFrom(List<Check> allchecks) {
        return allchecks.stream().map(c -> negativeSelectableFrom(c, false)).collect(Collectors.toList());
    }

}
