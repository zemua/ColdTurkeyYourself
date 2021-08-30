package devs.mrp.coolyourturkey.dtos.randomcheck;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

}
