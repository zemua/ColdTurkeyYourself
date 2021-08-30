package devs.mrp.coolyourturkey.dtos.randomcheck;

import devs.mrp.coolyourturkey.comun.MySelectable;

public abstract class APositiveCheckSelectable extends PositiveCheckImpl implements MySelectable {

    private boolean selected;

    @Override
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
