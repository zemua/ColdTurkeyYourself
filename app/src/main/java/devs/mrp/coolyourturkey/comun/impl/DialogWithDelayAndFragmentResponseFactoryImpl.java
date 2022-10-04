package devs.mrp.coolyourturkey.comun.impl;

import androidx.fragment.app.FragmentManager;

import devs.mrp.coolyourturkey.comun.DialogWithDelayAndFragmentResponse;
import devs.mrp.coolyourturkey.comun.DialogWithDelayAndFragmentResponseFactory;
import devs.mrp.coolyourturkey.comun.DialogWithDelayShower;

public class DialogWithDelayAndFragmentResponseFactoryImpl implements DialogWithDelayAndFragmentResponseFactory {
    @Override
    public DialogWithDelayShower getDialog(int iconResId, String title, String message, FragmentManager fragmentManager, String requestKey) {
        return new DialogWithDelayAndFragmentResponse(iconResId, title, message, fragmentManager, requestKey);
    }
}
