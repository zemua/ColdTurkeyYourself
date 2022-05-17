package devs.mrp.coolyourturkey.grupos.reviewer.tabs;

import android.widget.Switch;

import androidx.recyclerview.widget.RecyclerView;

import devs.mrp.coolyourturkey.R;

public abstract class AbstractSwitchesAdapter<VH extends RecyclerView.ViewHolder, ID, DATA> extends AbstractAdapter<VH, ID, DATA>{

    protected void setTextOfSwitch(Switch switchView, ID id) {
        if (ifInOtherGroup(id)) {
            switchView.setText(R.string.en_otro_grupo);
        } else {
            switchView.setText(R.string.switch_en_esta_lista);
        }
    }

    protected void setSwitchAccordingToDb(Switch switchView, ID id) {
        if (mapSettedElements.containsKey(id)){
            // assigned already
            if (ifInThisGroup(id)) {
                // to this group
                checkAndEnableSwitch(switchView);
            } else {
                // to another group
                uncheckAndDisableSwitch(switchView, id);
            }
        } else {
            // not yet assigned
            uncheckAndEnableSwitch(switchView);
        }
    }

    private void checkAndEnableSwitch(Switch switchView) {
        if (!switchView.isChecked()) {
            switchView.setChecked(true);
            switchView.setEnabled(true);
        }
    }

    private void uncheckAndDisableSwitch(Switch switchView, ID id) {
        if (switchView.isChecked()) {
            switchView.setChecked(false);
        }
        if (ifInOtherGroup(id)) {
            switchView.setEnabled(false);
        } else {
            switchView.setEnabled(true);
        }
    }

    private void uncheckAndEnableSwitch(Switch switchView) {
        if (switchView.isChecked()) {
            switchView.setChecked(false);
            switchView.setEnabled(true);
        }
    }

}
