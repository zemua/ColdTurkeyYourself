package devs.mrp.coolyourturkey.dtos.randomcheck;

import devs.mrp.coolyourturkey.comun.MySelectable;

public abstract class ANegativeCheckSelectable extends NegativeCheck implements MySelectable {

    private boolean selected;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}
