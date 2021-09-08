package devs.mrp.coolyourturkey.comun;

import androidx.fragment.app.Fragment;

@FunctionalInterface
public interface IMyTimePicker {

    public void pick(Fragment targetFragment, int requestCode, String tag);

}
