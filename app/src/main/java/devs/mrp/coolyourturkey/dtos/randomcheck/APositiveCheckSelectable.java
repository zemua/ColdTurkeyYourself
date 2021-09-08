package devs.mrp.coolyourturkey.dtos.randomcheck;

import devs.mrp.coolyourturkey.comun.MyNombrable;
import devs.mrp.coolyourturkey.comun.MySelectable;
import devs.mrp.coolyourturkey.comun.MySelectableAndNombrable;

public abstract class APositiveCheckSelectable extends PositiveCheckImpl implements MySelectableAndNombrable {

    private boolean selected;

    @Override
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
