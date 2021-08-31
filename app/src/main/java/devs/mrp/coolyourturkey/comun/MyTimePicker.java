package devs.mrp.coolyourturkey.comun;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import devs.mrp.coolyourturkey.configuracion.ConfiguracionFragment;

public class MyTimePicker implements IMyTimePicker{

    // request code for onActivityResult() method

    @Override
    public void pick(Fragment targetFragment, int requestCode, String tag) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setTargetFragment(targetFragment, requestCode);
        newFragment.show(targetFragment.getActivity().getSupportFragmentManager(), tag);
    }

}
