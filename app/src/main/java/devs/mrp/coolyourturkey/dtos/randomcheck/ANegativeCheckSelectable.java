package devs.mrp.coolyourturkey.dtos.randomcheck;

import devs.mrp.coolyourturkey.comun.MyNombrable;
import devs.mrp.coolyourturkey.comun.MySelectable;
import devs.mrp.coolyourturkey.comun.MySelectableAndNombrable;

public abstract class ANegativeCheckSelectable extends NegativeCheck implements MySelectableAndNombrable {

    private boolean selected;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}
